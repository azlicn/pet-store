package com.petstore.service;

import com.petstore.exception.UserInUseException;
import com.petstore.model.Pet;
import com.petstore.model.User;
import com.petstore.repository.PetRepository;
import com.petstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users (ADMIN only)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update user information
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update fields
        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            // Check if email is already in use by another user
            Optional<User> existingUser = userRepository.findByEmail(userDetails.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new RuntimeException("Email is already in use by another user");
            }
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getRoles() != null) {
            user.setRoles(userDetails.getRoles());
        }

        return userRepository.save(user);
    }

    /**
     * Delete user by ID (ADMIN only)
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if user owns any pets
        List<Pet> ownedPets = petRepository.findByOwner(user);
        
        // Check if user created any pets
        List<Pet> createdPets = petRepository.findByCreatedBy(id);
        
        // If user has any pets (owned or created), prevent deletion
        if (!ownedPets.isEmpty() || !createdPets.isEmpty()) {
            throw new UserInUseException(
                    id,
                    user.getEmail(),
                    ownedPets.size(),
                    createdPets.size()
            );
        }
        
        userRepository.delete(user);
    }

    /**
     * Check if user exists by ID
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
    
    /**
     * Get count of pets owned by a user
     * 
     * @param userId the user ID to check
     * @return the number of pets owned by this user
     */
    public int getUserOwnedPetCount(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return 0;
        }
        List<Pet> ownedPets = petRepository.findByOwner(user.get());
        return ownedPets.size();
    }
    
    /**
     * Get count of pets created by a user
     * 
     * @param userId the user ID to check
     * @return the number of pets created by this user
     */
    public int getUserCreatedPetCount(Long userId) {
        List<Pet> createdPets = petRepository.findByCreatedBy(userId);
        return createdPets.size();
    }
    
    /**
     * Check if a user can be safely deleted (has no pets owned or created)
     * 
     * @param userId the user ID to check
     * @return true if the user can be deleted, false otherwise
     */
    public boolean canDeleteUser(Long userId) {
        return getUserOwnedPetCount(userId) == 0 && getUserCreatedPetCount(userId) == 0;
    }
}