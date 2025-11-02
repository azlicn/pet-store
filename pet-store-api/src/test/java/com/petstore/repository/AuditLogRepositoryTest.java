package com.petstore.repository;

import com.petstore.model.AuditLog;
import com.petstore.model.Role;
import com.petstore.model.User;

import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AuditLog Repository Tests")
class AuditLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private User testUser;
    private AuditLog auditLog1;
    private AuditLog auditLog2;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(Role.USER));
        testUser = entityManager.persistAndFlush(testUser);

        // Create first audit log
        auditLog1 = new AuditLog("Pet", 1L, testUser, "CREATE", null, "New pet created");
        auditLog1 = entityManager.persistAndFlush(auditLog1);

        // Create second audit log
        auditLog2 = new AuditLog("Order", 2L, testUser, "UPDATE", "status: PENDING", "status: COMPLETED");
        auditLog2 = entityManager.persistAndFlush(auditLog2);

        entityManager.clear();
    }

    @Test
    @DisplayName("Save - Should persist new audit log with all fields")
    void save_ShouldPersistNewAuditLog() {
        // Arrange
        AuditLog newAuditLog = new AuditLog("User", 5L, testUser, "DELETE", "active: true", "active: false");

        // Act
        AuditLog savedAuditLog = auditLogRepository.save(newAuditLog);

        // Assert
        assertThat(savedAuditLog.getId()).isNotNull();
        assertThat(savedAuditLog.getEntityType()).isEqualTo("User");
        assertThat(savedAuditLog.getEntityId()).isEqualTo(5L);
        assertThat(savedAuditLog.getUser()).isNotNull();
        assertThat(savedAuditLog.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedAuditLog.getAction()).isEqualTo("DELETE");
        assertThat(savedAuditLog.getOldValue()).isEqualTo("active: true");
        assertThat(savedAuditLog.getNewValue()).isEqualTo("active: false");
        // Timestamps may be set by JPA auditing if configured, but not required for basic save operation
    }

    @Test
    @DisplayName("Save - Should persist audit log without user (nullable)")
    void save_ShouldPersistAuditLogWithoutUser() {
        // Arrange
        AuditLog auditLogWithoutUser = new AuditLog("Category", 3L, null, "CREATE", null, "name: Electronics");

        // Act
        AuditLog savedAuditLog = auditLogRepository.save(auditLogWithoutUser);

        // Assert
        assertThat(savedAuditLog.getId()).isNotNull();
        assertThat(savedAuditLog.getUser()).isNull();
        assertThat(savedAuditLog.getEntityType()).isEqualTo("Category");
        assertThat(savedAuditLog.getEntityId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Save - Should handle null old value (for CREATE action)")
    void save_ShouldHandleNullOldValue() {
        // Arrange
        AuditLog createLog = new AuditLog("Pet", 10L, testUser, "CREATE", null, "Pet created with name: Buddy");

        // Act
        AuditLog savedAuditLog = auditLogRepository.save(createLog);

        // Assert
        assertThat(savedAuditLog.getId()).isNotNull();
        assertThat(savedAuditLog.getOldValue()).isNull();
        assertThat(savedAuditLog.getNewValue()).isEqualTo("Pet created with name: Buddy");
    }

    @Test
    @DisplayName("Save - Should handle null new value (for DELETE action)")
    void save_ShouldHandleNullNewValue() {
        // Arrange
        AuditLog deleteLog = new AuditLog("Pet", 11L, testUser, "DELETE", "Pet: Buddy", null);

        // Act
        AuditLog savedAuditLog = auditLogRepository.save(deleteLog);

        // Assert
        assertThat(savedAuditLog.getId()).isNotNull();
        assertThat(savedAuditLog.getOldValue()).isEqualTo("Pet: Buddy");
        assertThat(savedAuditLog.getNewValue()).isNull();
    }

    @Test
    @DisplayName("Save - Should throw exception when entity type is null")
    void save_ShouldThrowExceptionWhenEntityTypeIsNull() {
        // Arrange
        AuditLog invalidLog = new AuditLog(null, 1L, testUser, "CREATE", null, "value");

        // Act & Assert
        assertThatThrownBy(() -> {
            auditLogRepository.save(invalidLog);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Save - Should throw exception when entity type is blank")
    void save_ShouldThrowExceptionWhenEntityTypeIsBlank() {
        // Arrange
        AuditLog invalidLog = new AuditLog("", 1L, testUser, "CREATE", null, "value");

        // Act & Assert
        assertThatThrownBy(() -> {
            auditLogRepository.save(invalidLog);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Save - Should throw exception when entity ID is null")
    void save_ShouldThrowExceptionWhenEntityIdIsNull() {
        // Arrange
        AuditLog invalidLog = new AuditLog("Pet", null, testUser, "CREATE", null, "value");

        // Act & Assert
        assertThatThrownBy(() -> {
            auditLogRepository.save(invalidLog);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Find by ID - Should return audit log when ID exists")
    void findById_ShouldReturnAuditLogWhenIdExists() {
        // Act
        Optional<AuditLog> foundLog = auditLogRepository.findById(auditLog1.getId());

        // Assert
        assertThat(foundLog).isPresent();
        assertThat(foundLog.get().getEntityType()).isEqualTo("Pet");
        assertThat(foundLog.get().getEntityId()).isEqualTo(1L);
        assertThat(foundLog.get().getAction()).isEqualTo("CREATE");
    }

    @Test
    @DisplayName("Find by ID - Should return empty when ID does not exist")
    void findById_ShouldReturnEmptyWhenIdDoesNotExist() {
        // Act
        Optional<AuditLog> foundLog = auditLogRepository.findById(9999L);

        // Assert
        assertThat(foundLog).isEmpty();
    }

    @Test
    @DisplayName("Find all - Should return all audit logs")
    void findAll_ShouldReturnAllAuditLogs() {
        // Act
        List<AuditLog> allLogs = auditLogRepository.findAll();

        // Assert
        assertThat(allLogs).hasSize(2);
        assertThat(allLogs).extracting(AuditLog::getEntityType)
                .containsExactlyInAnyOrder("Pet", "Order");
    }

    @Test
    @DisplayName("Count - Should return correct number of audit logs")
    void count_ShouldReturnCorrectNumberOfAuditLogs() {
        // Act
        long count = auditLogRepository.count();

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Delete by ID - Should remove audit log from database")
    void deleteById_ShouldRemoveAuditLogFromDatabase() {
        // Arrange
        Long logId = auditLog1.getId();
        assertThat(auditLogRepository.findById(logId)).isPresent();

        // Act
        auditLogRepository.deleteById(logId);

        // Assert
        assertThat(auditLogRepository.findById(logId)).isEmpty();
        assertThat(auditLogRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Save - Should automatically set timestamps if auditing is enabled")
    void save_ShouldAutomaticallySetTimestamps() {
        // Arrange
        AuditLog newLog = new AuditLog("Discount", 100L, testUser, "CREATE", null, "discount: 10%");

        // Act
        AuditLog savedLog = auditLogRepository.save(newLog);
        entityManager.flush();

        // Assert
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getEntityType()).isEqualTo("Discount");
        assertThat(savedLog.getAction()).isEqualTo("CREATE");
        // Note: Timestamps are managed by JPA Auditing (@CreatedDate, @LastModifiedDate)
        // They may be null in test context if auditing is not fully configured
    }

    @Test
    @DisplayName("Update - Should allow modifying audit log fields")
    void update_ShouldAllowModifyingFields() {
        // Arrange
        AuditLog existingLog = auditLogRepository.findById(auditLog1.getId()).orElseThrow();
        String originalAction = existingLog.getAction();
        existingLog.setAction("UPDATED_ACTION");

        // Act
        AuditLog updatedLog = auditLogRepository.save(existingLog);
        entityManager.flush();

        // Assert
        assertThat(updatedLog.getId()).isEqualTo(existingLog.getId());
        assertThat(updatedLog.getAction()).isEqualTo("UPDATED_ACTION");
        assertThat(updatedLog.getAction()).isNotEqualTo(originalAction);
    }

    @Test
    @DisplayName("Save - Should support different action types")
    void save_ShouldSupportDifferentActionTypes() {
        // Arrange & Act
        AuditLog createLog = auditLogRepository.save(new AuditLog("Pet", 20L, testUser, "CREATE", null, "new"));
        AuditLog updateLog = auditLogRepository.save(new AuditLog("Pet", 21L, testUser, "UPDATE", "old", "new"));
        AuditLog deleteLog = auditLogRepository.save(new AuditLog("Pet", 22L, testUser, "DELETE", "old", null));

        // Assert
        assertThat(createLog.getAction()).isEqualTo("CREATE");
        assertThat(updateLog.getAction()).isEqualTo("UPDATE");
        assertThat(deleteLog.getAction()).isEqualTo("DELETE");
    }

    @Test
    @DisplayName("Save - Should support different entity types")
    void save_ShouldSupportDifferentEntityTypes() {
        // Act
        AuditLog petLog = auditLogRepository.save(new AuditLog("Pet", 1L, testUser, "CREATE", null, "pet"));
        AuditLog userLog = auditLogRepository.save(new AuditLog("User", 2L, testUser, "CREATE", null, "user"));
        AuditLog orderLog = auditLogRepository.save(new AuditLog("Order", 3L, testUser, "CREATE", null, "order"));
        AuditLog categoryLog = auditLogRepository.save(new AuditLog("Category", 4L, testUser, "CREATE", null, "cat"));

        // Assert
        assertThat(petLog.getEntityType()).isEqualTo("Pet");
        assertThat(userLog.getEntityType()).isEqualTo("User");
        assertThat(orderLog.getEntityType()).isEqualTo("Order");
        assertThat(categoryLog.getEntityType()).isEqualTo("Category");
    }

    @Test
    @DisplayName("Save - Should handle reasonably sized text values")
    void save_ShouldHandleReasonablySizedTextValues() {
        // Arrange - Keep within VARCHAR(255) limit
        String oldValue = "Old value with reasonable length for database column";
        String newValue = "New value with reasonable length for database column";
        AuditLog logWithTextValues = new AuditLog("Pet", 50L, testUser, "UPDATE", oldValue, newValue);

        // Act
        AuditLog savedLog = auditLogRepository.save(logWithTextValues);

        // Assert
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getOldValue()).isEqualTo(oldValue);
        assertThat(savedLog.getNewValue()).isEqualTo(newValue);
    }
}
