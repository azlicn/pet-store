package com.petstore.service;

import com.petstore.enums.Role;
import com.petstore.exception.EmailAlreadyInUseException;
import com.petstore.exception.InvalidUserException;
import com.petstore.exception.UserInUseException;
import com.petstore.model.Pet;
import com.petstore.model.User;
import com.petstore.repository.PetRepository;
import com.petstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserService} covering user CRUD operations, edge cases,
 * and exception scenarios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testAdmin;

    /**
     * Initializes test users before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("encodedPassword123");
        testUser.setRoles(Set.of(Role.USER));

        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setEmail("admin@test.com");
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setPassword("encodedAdminPassword");
        testAdmin.setRoles(Set.of(Role.ADMIN, Role.USER));
    }

    /**
     * Tests that all users are returned from the service.
     */
    @Test
    @DisplayName("Get all users - Should return all users")
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> expectedUsers = Arrays.asList(testUser, testAdmin);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertThat(actualUsers).hasSize(2);
        assertThat(actualUsers).contains(testUser, testAdmin);
        verify(userRepository).findAll();
    }

    /**
     * Tests getting a user by ID when the user exists.
     */
    @Test
    @DisplayName("Get user by ID - Should return user when exists")
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> actualUser = userService.getUserById(1L);

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getEmail()).isEqualTo("user@test.com");
        assertThat(actualUser.get().getFirstName()).isEqualTo("John");
        verify(userRepository).findById(1L);
    }

    /**
     * Tests getting a user by ID when the user does not exist.
     */
    @Test
    @DisplayName("Get user by ID - Should return empty when user does not exist")
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> actualUser = userService.getUserById(999L);

        assertThat(actualUser).isEmpty();
        verify(userRepository).findById(999L);
    }

    /**
     * Tests getting a user by email when the user exists.
     */
    @Test
    @DisplayName("Get user by email - Should return user when exists")
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));

        Optional<User> actualUser = userService.getUserByEmail("user@test.com");

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getId()).isEqualTo(1L);
        assertThat(actualUser.get().getFirstName()).isEqualTo("John");
        verify(userRepository).findByEmail("user@test.com");
    }

    /**
     * Tests getting a user by email when the user does not exist.
     */
    @Test
    @DisplayName("Get user by email - Should return empty when user does not exist")
    void getUserByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        Optional<User> actualUser = userService.getUserByEmail("nonexistent@test.com");

        assertThat(actualUser).isEmpty();
        verify(userRepository).findByEmail("nonexistent@test.com");
    }

    /**
     * Tests updating all fields of a user when the user exists.
     */
    @Test
    @DisplayName("Update user - Should update all fields when user exists")
    void updateUser_WhenUserExists_ShouldUpdateAllFields() {
        User userDetails = new User();
        userDetails.setFirstName("Jane");
        userDetails.setLastName("Smith");
        userDetails.setEmail("jane@test.com");
        userDetails.setPassword("newPassword123");
        userDetails.setRoles(Set.of(Role.ADMIN));

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("jane@test.com");
        updatedUser.setPassword("encodedNewPassword");
        updatedUser.setRoles(Set.of(Role.ADMIN));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, userDetails);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("jane@test.com");
        assertThat(result.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(result.getRoles()).contains(Role.ADMIN);
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("jane@test.com");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Tests that updating a non-existent user throws an exception.
     */
    @Test
    @DisplayName("Update user - Should throw exception when user does not exist")
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        User userDetails = new User();
        userDetails.setFirstName("Jane");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, userDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 999");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests that updating a user with an email already in use throws an exception.
     */
    @Test
    @DisplayName("Update user - Should throw exception when email already in use")
    void updateUser_WhenEmailAlreadyInUse_ShouldThrowException() {
        User userDetails = new User();
        userDetails.setEmail("admin@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));

        assertThatThrownBy(() -> userService.updateUser(1L, userDetails))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessage("Email 'admin@test.com' is already in use");

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("admin@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests that updating a user with their current email is allowed.
     */
    @Test
    @DisplayName("Update user - Should allow update when email is same as current user")
    void updateUser_WhenEmailIsSameAsCurrentUser_ShouldAllowUpdate() {
        User userDetails = new User();
        userDetails.setEmail("user@test.com");
        userDetails.setFirstName("UpdatedJohn");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("user@test.com");
        updatedUser.setFirstName("UpdatedJohn");
        updatedUser.setLastName("Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, userDetails);

        assertThat(result.getFirstName()).isEqualTo("UpdatedJohn");
        assertThat(result.getEmail()).isEqualTo("user@test.com");
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("user@test.com");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Tests that only non-null fields are updated for a user.
     */
    @Test
    @DisplayName("Update user - Should only update non-null fields")
    void updateUser_WithNullFields_ShouldOnlyUpdateNonNullFields() {
        User userDetails = new User();
        userDetails.setFirstName("UpdatedJohn");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("UpdatedJohn");
        updatedUser.setLastName("Doe");
        updatedUser.setEmail("user@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, userDetails);

        assertThat(result.getFirstName()).isEqualTo("UpdatedJohn");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("user@test.com");
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }

    /**
     * Tests that password is not updated when the new password is empty.
     */
    @Test
    @DisplayName("Update user - Should not update password when empty")
    void updateUser_WithEmptyPassword_ShouldNotUpdatePassword() {
        User userDetails = new User();
        userDetails.setFirstName("UpdatedJohn");
        userDetails.setPassword("");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("UpdatedJohn");
        updatedUser.setPassword("encodedPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, userDetails);

        assertThat(result.getFirstName()).isEqualTo("UpdatedJohn");
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    /**
     * Tests deleting a user who exists and has no pets.
     */
    @Test
    @DisplayName("Delete user - Should delete user when exists and has no pets")
    void deleteUser_WhenUserExistsAndHasNoPets_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(Arrays.asList()); // Empty list - no owned pets
        when(petRepository.findByCreatedBy(1L)).thenReturn(Arrays.asList()); // Empty list - no created pets

        userService.deleteUser(1L);

        verify(userRepository, atLeastOnce()).findById(1L);
        verify(petRepository).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
        verify(userRepository).delete(testUser);
    }

    /**
     * Tests that deleting a non-existent user throws an exception.
     */
    @Test
    @DisplayName("Delete user - Should throw exception when user does not exist")
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 999");

        verify(userRepository, atLeastOnce()).findById(999L);
        verify(petRepository, never()).findByOwner(any());
        verify(petRepository, never()).findByCreatedBy(any());
        verify(userRepository, never()).delete(any(User.class));
    }

    /**
     * Tests that deleting a user who owns pets throws a UserInUseException.
     */
    @Test
    @DisplayName("Delete user - Should throw exception when user owns pets")
    void deleteUser_WhenUserOwnsPets_ShouldThrowUserInUseException() {
        Pet ownedPet1 = new Pet();
        ownedPet1.setId(1L);
        ownedPet1.setName("Buddy");

        Pet ownedPet2 = new Pet();
        ownedPet2.setId(2L);
        ownedPet2.setName("Max");

        List<Pet> ownedPets = Arrays.asList(ownedPet1, ownedPet2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(ownedPets);
        when(petRepository.findByCreatedBy(1L)).thenReturn(Arrays.asList()); // No created pets

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserInUseException.class)
                .hasMessageContaining("Cannot delete user 'user@test.com' (ID: 1)")
                .hasMessageContaining("ownership of 2 pet(s)");

        verify(userRepository, atLeastOnce()).findById(1L);
        verify(petRepository, atLeast(2)).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    /**
     * Tests that deleting a user who created pets throws a UserInUseException.
     */
    @Test
    @DisplayName("Delete user - Should throw exception when user created pets")
    void deleteUser_WhenUserCreatedPets_ShouldThrowUserInUseException() {
        Pet createdPet1 = new Pet();
        createdPet1.setId(3L);
        createdPet1.setName("Luna");

        List<Pet> createdPets = Arrays.asList(createdPet1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(Arrays.asList()); // No owned pets
        when(petRepository.findByCreatedBy(1L)).thenReturn(createdPets);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserInUseException.class)
                .hasMessageContaining("Cannot delete user 'user@test.com' (ID: 1)")
                .hasMessageContaining("created 1 pet(s)");

        verify(userRepository, atLeastOnce()).findById(1L);
        verify(petRepository, atLeast(2)).findByOwner(testUser);
        verify(petRepository, atLeast(2)).findByCreatedBy(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    /**
     * Tests that deleting a user who owns and created pets throws a
     * UserInUseException.
     */
    @Test
    @DisplayName("Delete user - Should throw exception when user owns and created pets")
    void deleteUser_WhenUserOwnsAndCreatedPets_ShouldThrowUserInUseException() {
        Pet ownedPet = new Pet();
        ownedPet.setId(1L);
        ownedPet.setName("Buddy");

        Pet createdPet = new Pet();
        createdPet.setId(2L);
        createdPet.setName("Luna");

        List<Pet> ownedPets = Arrays.asList(ownedPet);
        List<Pet> createdPets = Arrays.asList(createdPet);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(ownedPets);
        when(petRepository.findByCreatedBy(1L)).thenReturn(createdPets);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserInUseException.class)
                .hasMessageContaining("Cannot delete user 'user@test.com' (ID: 1)")
                .hasMessageContaining("ownership of 1 pet(s) and created 1 pet(s)");

        verify(userRepository, atLeastOnce()).findById(1L);
        verify(petRepository, atLeast(2)).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    /**
     * Tests that existsById returns true when the user exists.
     */
    @Test
    @DisplayName("Exists by ID - Should return true when user exists")
    void existsById_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean exists = userService.existsById(1L);

        assertThat(exists).isTrue();
        verify(userRepository).existsById(1L);
    }

    /**
     * Tests that existsById returns false when the user does not exist.
     */
    @Test
    @DisplayName("Exists by ID - Should return false when user does not exist")
    void existsById_WhenUserDoesNotExist_ShouldReturnFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean exists = userService.existsById(999L);

        assertThat(exists).isFalse();
        verify(userRepository).existsById(999L);
    }

    /**
     * Tests that getUserByEmail returns empty when email is null.
     */
    @Test
    @DisplayName("Get user by email - Should return empty when email is null")
    void getUserByEmail_WhenEmailIsNull_ShouldReturnEmpty() {
        Optional<User> actualUser = userService.getUserByEmail(null);
        assertThat(actualUser).isEmpty();
        verify(userRepository, never()).findByEmail(anyString());
    }

    /**
     * Tests that getUserByEmail returns empty when email is an empty string.
     */
    @Test
    @DisplayName("Get user by email - Should return empty when email is empty string")
    void getUserByEmail_WhenEmailIsEmpty_ShouldReturnEmpty() {
        Optional<User> actualUser = userService.getUserByEmail("");
        assertThat(actualUser).isEmpty();
        verify(userRepository, never()).findByEmail(anyString());
    }

    /**
     * Tests that updating a user with a null ID throws an exception.
     */
    @Test
    @DisplayName("Update user - Should throw exception when ID is null")
    void updateUser_WhenIdIsNull_ShouldThrowException() {
        User userDetails = new User();
        userDetails.setFirstName("Jane");
        assertThatThrownBy(() -> userService.updateUser(null, userDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: null");
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests that updating a user with null details throws an exception.
     */
    @Test
    @DisplayName("Update user - Should throw exception when details are null")
    void updateUser_WhenDetailsAreNull_ShouldThrowException() {
        assertThatThrownBy(() -> userService.updateUser(1L, null))
                .isInstanceOf(InvalidUserException.class)
                .hasMessageContaining("Updated user cannot be null");
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests that deleting a user with a null ID throws an exception.
     */
    @Test
    @DisplayName("Delete user - Should throw exception when ID is null")
    void deleteUser_WhenIdIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> userService.deleteUser(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: null");
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).delete(any(User.class));
    }
}