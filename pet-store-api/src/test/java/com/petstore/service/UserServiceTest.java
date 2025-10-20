package com.petstore.service;

import com.petstore.exception.UserInUseException;
import com.petstore.model.Pet;
import com.petstore.model.User;
import com.petstore.model.Role;
import com.petstore.repository.PetRepository;
import com.petstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
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

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> expectedUsers = Arrays.asList(testUser, testAdmin);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertThat(actualUsers).hasSize(2);
        assertThat(actualUsers).contains(testUser, testAdmin);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> actualUser = userService.getUserById(1L);

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getEmail()).isEqualTo("user@test.com");
        assertThat(actualUser.get().getFirstName()).isEqualTo("John");
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> actualUser = userService.getUserById(999L);

        assertThat(actualUser).isEmpty();
        verify(userRepository).findById(999L);
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));

        Optional<User> actualUser = userService.getUserByEmail("user@test.com");

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getId()).isEqualTo(1L);
        assertThat(actualUser.get().getFirstName()).isEqualTo("John");
        verify(userRepository).findByEmail("user@test.com");
    }

    @Test
    void getUserByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        Optional<User> actualUser = userService.getUserByEmail("nonexistent@test.com");

        assertThat(actualUser).isEmpty();
        verify(userRepository).findByEmail("nonexistent@test.com");
    }

    @Test
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

    @Test
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

    @Test
    void updateUser_WhenEmailAlreadyInUse_ShouldThrowException() {
        User userDetails = new User();
        userDetails.setEmail("admin@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));

        assertThatThrownBy(() -> userService.updateUser(1L, userDetails))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Email is already in use by another user");
        
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("admin@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
    void deleteUser_WhenUserExistsAndHasNoPets_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(Arrays.asList()); // Empty list - no owned pets
        when(petRepository.findByCreatedBy(1L)).thenReturn(Arrays.asList()); // Empty list - no created pets

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with id: 999");
        
        verify(userRepository).findById(999L);
        verify(petRepository, never()).findByOwner(any());
        verify(petRepository, never()).findByCreatedBy(any());
        verify(userRepository, never()).delete(any(User.class));
    }
    
    @Test
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
        
        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
    
    @Test
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
        
        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
    
    @Test
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
        
        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void existsById_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean exists = userService.existsById(1L);

        assertThat(exists).isTrue();
        verify(userRepository).existsById(1L);
    }

    @Test
    void existsById_WhenUserDoesNotExist_ShouldReturnFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean exists = userService.existsById(999L);

        assertThat(exists).isFalse();
        verify(userRepository).existsById(999L);
    }
    
    @Test
    void getUserOwnedPetCount_WhenUserExistsAndOwnsPets_ShouldReturnCount() {
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        List<Pet> ownedPets = Arrays.asList(pet1, pet2);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(ownedPets);

        int count = userService.getUserOwnedPetCount(1L);

        assertThat(count).isEqualTo(2);
        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
    }
    
    @Test
    void getUserOwnedPetCount_WhenUserDoesNotExist_ShouldReturnZero() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        int count = userService.getUserOwnedPetCount(999L);

        assertThat(count).isEqualTo(0);
        verify(userRepository).findById(999L);
        verify(petRepository, never()).findByOwner(any());
    }
    
    @Test
    void getUserCreatedPetCount_WhenUserCreatedPets_ShouldReturnCount() {
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        Pet pet3 = new Pet();
        List<Pet> createdPets = Arrays.asList(pet1, pet2, pet3);
        
        when(petRepository.findByCreatedBy(1L)).thenReturn(createdPets);

        int count = userService.getUserCreatedPetCount(1L);

        assertThat(count).isEqualTo(3);
        verify(petRepository).findByCreatedBy(1L);
    }
    
    @Test
    void getUserCreatedPetCount_WhenUserCreatedNoPets_ShouldReturnZero() {
        when(petRepository.findByCreatedBy(1L)).thenReturn(Arrays.asList());

        int count = userService.getUserCreatedPetCount(1L);

        assertThat(count).isEqualTo(0);
        verify(petRepository).findByCreatedBy(1L);
    }
    
    @Test
    void canDeleteUser_WhenUserHasNoPets_ShouldReturnTrue() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(Arrays.asList());
        when(petRepository.findByCreatedBy(1L)).thenReturn(Arrays.asList());

        boolean canDelete = userService.canDeleteUser(1L);

        assertThat(canDelete).isTrue();
        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
    }
    
    @Test
    void canDeleteUser_WhenUserOwnsPets_ShouldReturnFalse() {
        Pet pet = new Pet();
        List<Pet> ownedPets = Arrays.asList(pet);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(ownedPets);

        boolean canDelete = userService.canDeleteUser(1L);

        assertThat(canDelete).isFalse();
        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
    }
    
    @Test
    void canDeleteUser_WhenUserCreatedPets_ShouldReturnFalse() {
        Pet pet = new Pet();
        List<Pet> createdPets = Arrays.asList(pet);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwner(testUser)).thenReturn(Arrays.asList());
        when(petRepository.findByCreatedBy(1L)).thenReturn(createdPets);

        boolean canDelete = userService.canDeleteUser(1L);

        assertThat(canDelete).isFalse();
        verify(userRepository).findById(1L);
        verify(petRepository).findByOwner(testUser);
        verify(petRepository).findByCreatedBy(1L);
    }
}