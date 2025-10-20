package com.petstore.repository;

import com.petstore.config.TestDatabaseConfig;
import com.petstore.model.User;
import com.petstore.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TestDatabaseConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Create test users
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
    void findByEmail_ShouldReturnUserWhenEmailExists() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("user@test.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("user@test.com");
        assertThat(foundUser.get().getFirstName()).isEqualTo("John");
        assertThat(foundUser.get().getLastName()).isEqualTo("Doe");
        assertThat(foundUser.get().getRoles()).containsExactly(Role.USER);
    }

    @Test
    void findByEmail_ShouldReturnEmptyWhenEmailDoesNotExist() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@test.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByEmail_ShouldBeCaseInsensitive() {
        // When
        Optional<User> foundUser1 = userRepository.findByEmail("USER@TEST.COM");
        Optional<User> foundUser2 = userRepository.findByEmail("User@Test.Com");
        Optional<User> foundUser3 = userRepository.findByEmail("user@test.com");

        // Then
        assertThat(foundUser1).isPresent();
        assertThat(foundUser2).isPresent();
        assertThat(foundUser3).isPresent();
        
        assertThat(foundUser1.get().getId()).isEqualTo(testUser.getId());
        assertThat(foundUser2.get().getId()).isEqualTo(testUser.getId());
        assertThat(foundUser3.get().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void existsByEmail_ShouldReturnTrueWhenEmailExists() {
        // When
        boolean exists = userRepository.existsByEmail("user@test.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalseWhenEmailDoesNotExist() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_ShouldBeCaseInsensitive() {
        // When
        boolean exists1 = userRepository.existsByEmail("USER@TEST.COM");
        boolean exists2 = userRepository.existsByEmail("User@Test.Com");
        boolean exists3 = userRepository.existsByEmail("user@test.com");

        // Then
        assertThat(exists1).isTrue();
        assertThat(exists2).isTrue();
        assertThat(exists3).isTrue();
    }

    @Test
    void save_ShouldPersistNewUserWithAllFields() {
        // Given
        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setPassword("encodedPassword456");
        newUser.setRoles(Set.of(Role.USER));

        // When
        User savedUser = userRepository.save(newUser);

        // Then
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
    void save_ShouldUpdateExistingUser() {
        // Given
        testUser.setFirstName("UpdatedJohn");
        testUser.setLastName("UpdatedDoe");

        // When
        User updatedUser = userRepository.save(testUser);

        // Then
        assertThat(updatedUser.getId()).isEqualTo(testUser.getId());
        assertThat(updatedUser.getFirstName()).isEqualTo("UpdatedJohn");
        assertThat(updatedUser.getLastName()).isEqualTo("UpdatedDoe");
        assertThat(updatedUser.getEmail()).isEqualTo("user@test.com");
        assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
    }

    @Test
    void save_ShouldThrowExceptionForDuplicateEmail() {
        // Given
        User duplicateUser = new User();
        duplicateUser.setEmail("user@test.com"); // Same email as testUser
        duplicateUser.setFirstName("Duplicate");
        duplicateUser.setLastName("User");
        duplicateUser.setPassword("password");
        duplicateUser.setRoles(Set.of(Role.USER));

        // When & Then
        assertThatThrownBy(() -> {
            userRepository.save(duplicateUser);
            entityManager.flush(); // Force the constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_ShouldHandleMultipleRoles() {
        // Given
        User multiRoleUser = new User();
        multiRoleUser.setEmail("multirole@test.com");
        multiRoleUser.setFirstName("Multi");
        multiRoleUser.setLastName("Role");
        multiRoleUser.setPassword("password");
        multiRoleUser.setRoles(Set.of(Role.USER, Role.ADMIN));

        // When
        User savedUser = userRepository.save(multiRoleUser);

        // Then
        assertThat(savedUser.getRoles()).hasSize(2);
        assertThat(savedUser.getRoles()).containsExactlyInAnyOrder(Role.USER, Role.ADMIN);
    }

    @Test
    void deleteById_ShouldRemoveUserFromDatabase() {
        // Given
        Long userId = testUser.getId();
        assertThat(userRepository.findById(userId)).isPresent();

        // When
        userRepository.deleteById(userId);

        // Then
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // When
        Iterable<User> allUsers = userRepository.findAll();

        // Then
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting(User::getEmail)
            .containsExactlyInAnyOrder("user@test.com", "admin@test.com");
    }

    @Test
    void count_ShouldReturnCorrectNumberOfUsers() {
        // When
        long userCount = userRepository.count();

        // Then
        assertThat(userCount).isEqualTo(2);
    }

    @Test
    void findById_ShouldReturnUserWhenIdExists() {
        // When
        Optional<User> foundUser = userRepository.findById(testUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void findById_ShouldReturnEmptyWhenIdDoesNotExist() {
        // When
        Optional<User> foundUser = userRepository.findById(999L);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void save_ShouldHandleEmptyRoles() {
        // Given
        User userWithoutRoles = new User();
        userWithoutRoles.setEmail("noroles@test.com");
        userWithoutRoles.setFirstName("No");
        userWithoutRoles.setLastName("Roles");
        userWithoutRoles.setPassword("password");
        userWithoutRoles.setRoles(Set.of());

        // When
        User savedUser = userRepository.save(userWithoutRoles);

        // Then
        assertThat(savedUser.getRoles()).isEmpty();
    }

    @Test
    void save_ShouldTrimAndNormalizeEmail() {
        // Given
        User userWithSpacedEmail = new User();
        userWithSpacedEmail.setEmail("  SPACED@TEST.COM  ");
        userWithSpacedEmail.setFirstName("Spaced");
        userWithSpacedEmail.setLastName("Email");
        userWithSpacedEmail.setPassword("password");
        userWithSpacedEmail.setRoles(Set.of(Role.USER));

        // When
        User savedUser = userRepository.save(userWithSpacedEmail);

        // Then
        assertThat(savedUser.getEmail()).isEqualTo("spaced@test.com");
    }
}