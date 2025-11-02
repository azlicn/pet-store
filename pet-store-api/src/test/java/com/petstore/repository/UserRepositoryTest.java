package com.petstore.repository;

import com.petstore.enums.Role;
import com.petstore.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setEmail("user@test.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("encodedPassword123");
        testUser.setRoles(Set.of(Role.USER));
        testUser = entityManager.persistAndFlush(testUser);

        adminUser = new User();
        adminUser.setEmail("admin@test.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword("encodedAdminPassword");
        adminUser.setRoles(Set.of(Role.ADMIN, Role.USER));
        adminUser = entityManager.persistAndFlush(adminUser);

        entityManager.clear();
    }

    @Test
    @DisplayName("Find by email - Should return user when email exists")
    void findByEmail_ShouldReturnUserWhenEmailExists() {

        Optional<User> foundUser = userRepository.findByEmail("user@test.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("user@test.com");
        assertThat(foundUser.get().getFirstName()).isEqualTo("John");
        assertThat(foundUser.get().getLastName()).isEqualTo("Doe");
        assertThat(foundUser.get().getRoles()).containsExactly(Role.USER);
    }

    @Test
    @DisplayName("Find by email - Should return empty when email does not exist")
    void findByEmail_ShouldReturnEmptyWhenEmailDoesNotExist() {

        Optional<User> foundUser = userRepository.findByEmail("nonexistent@test.com");

        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Find by email - Should be case insensitive")
    void findByEmail_ShouldBeCaseInsensitive() {

        Optional<User> foundUser1 = userRepository.findByEmail("USER@TEST.COM");
        Optional<User> foundUser2 = userRepository.findByEmail("User@Test.Com");
        Optional<User> foundUser3 = userRepository.findByEmail("user@test.com");

        assertThat(foundUser1).isPresent();
        assertThat(foundUser2).isPresent();
        assertThat(foundUser3).isPresent();

        assertThat(foundUser1.get().getId()).isEqualTo(testUser.getId());
        assertThat(foundUser2.get().getId()).isEqualTo(testUser.getId());
        assertThat(foundUser3.get().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Exists by email - Should return true when email exists")
    void existsByEmail_ShouldReturnTrueWhenEmailExists() {

        boolean exists = userRepository.existsByEmail("user@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Exists by email - Should return false when email does not exist")
    void existsByEmail_ShouldReturnFalseWhenEmailDoesNotExist() {

        boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Exists by email - Should be case insensitive")
    void existsByEmail_ShouldBeCaseInsensitive() {

        boolean exists1 = userRepository.existsByEmail("USER@TEST.COM");
        boolean exists2 = userRepository.existsByEmail("User@Test.Com");
        boolean exists3 = userRepository.existsByEmail("user@test.com");

        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
        assertThat(exists3).isTrue();
    }

    @Test
    @DisplayName("Save - Should persist new user with all fields")
    void save_ShouldPersistNewUserWithAllFields() {

        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setPassword("encodedPassword456");
        newUser.setRoles(Set.of(Role.USER));

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("new@test.com");
        assertThat(savedUser.getFirstName()).isEqualTo("Jane");
        assertThat(savedUser.getLastName()).isEqualTo("Smith");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword456");
        assertThat(savedUser.getRoles()).containsExactly(Role.USER);
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Save - Should update existing user")
    void save_ShouldUpdateExistingUser() {
        
        User userToUpdate = userRepository.findById(testUser.getId()).get();
        userToUpdate.setFirstName("UpdatedJohn");
        userToUpdate.setLastName("UpdatedDoe");

        User updatedUser = userRepository.save(userToUpdate);

        assertThat(updatedUser.getId()).isEqualTo(testUser.getId());
        assertThat(updatedUser.getFirstName()).isEqualTo("UpdatedJohn");
        assertThat(updatedUser.getLastName()).isEqualTo("UpdatedDoe");
        assertThat(updatedUser.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("Save - Should throw exception for duplicate email")
    void save_ShouldThrowExceptionForDuplicateEmail() {

        User duplicateUser = new User();
        duplicateUser.setEmail("user@test.com");
        duplicateUser.setFirstName("Duplicate");
        duplicateUser.setLastName("User");
        duplicateUser.setPassword("password");
        duplicateUser.setRoles(Set.of(Role.USER));

        assertThatThrownBy(() -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save - Should handle multiple roles")
    void save_ShouldHandleMultipleRoles() {

        User multiRoleUser = new User();
        multiRoleUser.setEmail("multirole@test.com");
        multiRoleUser.setFirstName("Multi");
        multiRoleUser.setLastName("Role");
        multiRoleUser.setPassword("password");
        multiRoleUser.setRoles(Set.of(Role.USER, Role.ADMIN));

        User savedUser = userRepository.save(multiRoleUser);

        assertThat(savedUser.getRoles()).hasSize(2);
        assertThat(savedUser.getRoles()).containsExactlyInAnyOrder(Role.USER, Role.ADMIN);
    }

    @Test
    @DisplayName("Delete by ID - Should remove user from database")
    void deleteById_ShouldRemoveUserFromDatabase() {

        Long userId = testUser.getId();
        assertThat(userRepository.findById(userId)).isPresent();

        userRepository.deleteById(userId);

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("Find all - Should return all users")
    void findAll_ShouldReturnAllUsers() {

        Iterable<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting(User::getEmail)
                .containsExactlyInAnyOrder("user@test.com", "admin@test.com");
    }

    @Test
    @DisplayName("Count - Should return correct number of users")
    void count_ShouldReturnCorrectNumberOfUsers() {

        long userCount = userRepository.count();

        assertThat(userCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Find by ID - Should return user when ID exists")
    void findById_ShouldReturnUserWhenIdExists() {

        Optional<User> foundUser = userRepository.findById(testUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("Find by ID - Should return empty when ID does not exist")
    void findById_ShouldReturnEmptyWhenIdDoesNotExist() {

        Optional<User> foundUser = userRepository.findById(999L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Save - Should handle empty roles")
    void save_ShouldHandleEmptyRoles() {

        User userWithoutRoles = new User();
        userWithoutRoles.setEmail("noroles@test.com");
        userWithoutRoles.setFirstName("No");
        userWithoutRoles.setLastName("Roles");
        userWithoutRoles.setPassword("password");
        userWithoutRoles.setRoles(Set.of());

        User savedUser = userRepository.save(userWithoutRoles);

        assertThat(savedUser.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("Save - Should trim and normalize email")
    void save_ShouldTrimAndNormalizeEmail() {

        User userWithSpacedEmail = new User();
        userWithSpacedEmail.setEmail("  SPACED@TEST.COM  ");
        userWithSpacedEmail.setFirstName("Spaced");
        userWithSpacedEmail.setLastName("Email");
        userWithSpacedEmail.setPassword("password");
        userWithSpacedEmail.setRoles(Set.of(Role.USER));

        User savedUser = userRepository.save(userWithSpacedEmail);

        assertThat(savedUser.getEmail()).isEqualTo("spaced@test.com");
    }
}