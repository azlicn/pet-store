package com.petstore.service;

import com.petstore.exception.EmailAlreadyInUseException;
import com.petstore.exception.InvalidUserException;
import com.petstore.exception.UserInUseException;
import com.petstore.exception.UserNotFoundException;
import com.petstore.model.Pet;
import com.petstore.model.User;
import com.petstore.repository.PetRepository;
import com.petstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user operations in the pet store
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PetRepository petRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PetRepository petRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Gets all users in the system (ADMIN only)
     *
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID
     *
     * @param id the ID of the user to retrieve
     * @return the user if found
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by their email address
     *
     * @param email the email address to search for
     * @return the user if found
     */
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Updates a user's information
     *
     * @param id          the ID of the user to update
     * @param updatedUser the new user details
     * @return the updated user
     * @throws UserNotFoundException      if user not found
     * @throws EmailAlreadyInUseException if the new email is already in use
     */
    public User updateUser(Long id, User updatedUser) {
        if (id == null) {
            throw new UserNotFoundException(id);
        }
        if (updatedUser == null) {
            throw new InvalidUserException("Updated user cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Update fields
        if (updatedUser.getFirstName() != null) {
            user.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            user.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getEmail() != null) {
            // Check if email is already in use by another user
            Optional<User> existingUser = userRepository.findByEmail(updatedUser.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new EmailAlreadyInUseException(updatedUser.getEmail());
            }
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getRoles() != null) {
            user.setRoles(updatedUser.getRoles());
        }

        return userRepository.save(user);
    }

    /**
     * Deletes a user by their ID (ADMIN only)
     *
     * @param id the ID of the user to delete
     * @throws UserNotFoundException if user not found
     * @throws UserInUseException    if user owns or created pets that still exist
     */
    public void deleteUser(Long id) {
        if (id == null) {
            throw new UserNotFoundException(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!canDeleteUser(id)) {
            int ownedCount = getUserOwnedPetCount(id);
            int createdCount = getUserCreatedPetCount(id);
            StringBuilder message = new StringBuilder();
            message.append(String.format("Cannot delete user '%s' (ID: %d) because they have ", user.getEmail(), id));
            if (ownedCount > 0 && createdCount > 0) {
                message.append(String.format("ownership of %d pet(s) and created %d pet(s)", ownedCount, createdCount));
            } else if (ownedCount > 0) {
                message.append(String.format("ownership of %d pet(s)", ownedCount));
            } else if (createdCount > 0) {
                message.append(String.format("created %d pet(s)", createdCount));
            }
            message.append(" that still exist in the database");
            throw new UserInUseException(message.toString());
        }

        userRepository.delete(user);
    }

    /**
     * Checks if a user exists by their ID
     *
     * @param id the ID to check
     * @return true if user exists, false otherwise
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Checks if a user exists by their ID
     *
     * @param id the ID to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Gets the number of pets owned by a user
     *
     * @param userId the ID of the user
     * @return the number of pets owned
     */
    private int getUserOwnedPetCount(Long userId) {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return 0;
        }
        List<Pet> ownedPets = petRepository.findByOwner(user.get());
        return ownedPets.size();
    }

    /**
     * Gets the number of pets created by a user
     *
     * @param userId the ID of the user
     * @return the number of pets created
     */
    private int getUserCreatedPetCount(Long userId) {
        List<Pet> createdPets = petRepository.findByCreatedBy(userId);
        return createdPets.size();
    }

    /**
     * Checks if a user can be safely deleted
     *
     * @param userId the ID of the user
     * @return true if user can be deleted, false otherwise
     */
    private boolean canDeleteUser(Long userId) {
        return getUserOwnedPetCount(userId) == 0 && getUserCreatedPetCount(userId) == 0;
    }

    /**
     * Saves a user to the repository
     *
     * @param user the user to save
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}