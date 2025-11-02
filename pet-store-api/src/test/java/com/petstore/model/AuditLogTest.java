package com.petstore.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AuditLog Model Tests")
class AuditLogTest {

    private User testUser;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(Role.USER));

        auditLog = new AuditLog("Pet", 100L, testUser, "CREATE", null, "New pet created");
    }

    @Test
    @DisplayName("Constructor - Should create audit log with all required fields")
    void constructor_ShouldCreateAuditLogWithAllFields() {
        // Assert
        assertThat(auditLog.getEntityType()).isEqualTo("Pet");
        assertThat(auditLog.getEntityId()).isEqualTo(100L);
        assertThat(auditLog.getUser()).isEqualTo(testUser);
        assertThat(auditLog.getAction()).isEqualTo("CREATE");
        assertThat(auditLog.getOldValue()).isNull();
        assertThat(auditLog.getNewValue()).isEqualTo("New pet created");
    }

    @Test
    @DisplayName("Constructor - Should handle null user")
    void constructor_ShouldHandleNullUser() {
        // Act
        AuditLog logWithoutUser = new AuditLog("Order", 200L, null, "CREATE", null, "Order created");

        // Assert
        assertThat(logWithoutUser.getUser()).isNull();
        assertThat(logWithoutUser.getEntityType()).isEqualTo("Order");
    }

    @Test
    @DisplayName("Constructor - Should handle null old value for CREATE action")
    void constructor_ShouldHandleNullOldValueForCreate() {
        // Act
        AuditLog createLog = new AuditLog("User", 300L, testUser, "CREATE", null, "User registered");

        // Assert
        assertThat(createLog.getOldValue()).isNull();
        assertThat(createLog.getNewValue()).isEqualTo("User registered");
    }

    @Test
    @DisplayName("Constructor - Should handle null new value for DELETE action")
    void constructor_ShouldHandleNullNewValueForDelete() {
        // Act
        AuditLog deleteLog = new AuditLog("Pet", 400L, testUser, "DELETE", "Pet: Buddy", null);

        // Assert
        assertThat(deleteLog.getOldValue()).isEqualTo("Pet: Buddy");
        assertThat(deleteLog.getNewValue()).isNull();
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for ID")
    void gettersAndSetters_ShouldWorkForId() {
        // Act
        auditLog.setId(999L);

        // Assert
        assertThat(auditLog.getId()).isEqualTo(999L);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for entity type")
    void gettersAndSetters_ShouldWorkForEntityType() {
        // Act
        auditLog.setEntityType("Order");

        // Assert
        assertThat(auditLog.getEntityType()).isEqualTo("Order");
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for entity ID")
    void gettersAndSetters_ShouldWorkForEntityId() {
        // Act
        auditLog.setEntityId(500L);

        // Assert
        assertThat(auditLog.getEntityId()).isEqualTo(500L);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for user")
    void gettersAndSetters_ShouldWorkForUser() {
        // Arrange
        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("jane@example.com");

        // Act
        auditLog.setUser(newUser);

        // Assert
        assertThat(auditLog.getUser()).isEqualTo(newUser);
        assertThat(auditLog.getUser().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for action")
    void gettersAndSetters_ShouldWorkForAction() {
        // Act
        auditLog.setAction("UPDATE");

        // Assert
        assertThat(auditLog.getAction()).isEqualTo("UPDATE");
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for old value")
    void gettersAndSetters_ShouldWorkForOldValue() {
        // Act
        auditLog.setOldValue("status: PENDING");

        // Assert
        assertThat(auditLog.getOldValue()).isEqualTo("status: PENDING");
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for new value")
    void gettersAndSetters_ShouldWorkForNewValue() {
        // Act
        auditLog.setNewValue("status: COMPLETED");

        // Assert
        assertThat(auditLog.getNewValue()).isEqualTo("status: COMPLETED");
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for createdAt")
    void gettersAndSetters_ShouldWorkForCreatedAt() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        auditLog.setCreatedAt(now);

        // Assert
        assertThat(auditLog.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for updatedAt")
    void gettersAndSetters_ShouldWorkForUpdatedAt() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        auditLog.setUpdatedAt(now);

        // Assert
        assertThat(auditLog.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for createdBy")
    void gettersAndSetters_ShouldWorkForCreatedBy() {
        // Act
        auditLog.setCreatedBy(123L);

        // Assert
        assertThat(auditLog.getCreatedBy()).isEqualTo(123L);
    }

    @Test
    @DisplayName("Should support CREATE action with detailed change information")
    void shouldSupportCreateActionWithDetails() {
        // Act
        AuditLog createLog = new AuditLog(
            "Pet",
            10L,
            testUser,
            "CREATE",
            null,
            "Pet created: name=Buddy, species=Dog, breed=Golden Retriever"
        );

        // Assert
        assertThat(createLog.getAction()).isEqualTo("CREATE");
        assertThat(createLog.getOldValue()).isNull();
        assertThat(createLog.getNewValue()).contains("Buddy", "Dog", "Golden Retriever");
    }

    @Test
    @DisplayName("Should support UPDATE action with before/after values")
    void shouldSupportUpdateActionWithBeforeAfter() {
        // Act
        AuditLog updateLog = new AuditLog(
            "Order",
            20L,
            testUser,
            "UPDATE",
            "status=PENDING, total=100.00",
            "status=COMPLETED, total=150.00"
        );

        // Assert
        assertThat(updateLog.getAction()).isEqualTo("UPDATE");
        assertThat(updateLog.getOldValue()).contains("PENDING", "100.00");
        assertThat(updateLog.getNewValue()).contains("COMPLETED", "150.00");
    }

    @Test
    @DisplayName("Should support DELETE action with final state")
    void shouldSupportDeleteActionWithFinalState() {
        // Act
        AuditLog deleteLog = new AuditLog(
            "User",
            30L,
            testUser,
            "DELETE",
            "User deleted: email=old@example.com, role=USER",
            null
        );

        // Assert
        assertThat(deleteLog.getAction()).isEqualTo("DELETE");
        assertThat(deleteLog.getOldValue()).contains("old@example.com", "USER");
        assertThat(deleteLog.getNewValue()).isNull();
    }

    @Test
    @DisplayName("Should support tracking multiple entity types")
    void shouldSupportMultipleEntityTypes() {
        // Act
        AuditLog petLog = new AuditLog("Pet", 1L, testUser, "CREATE", null, "Pet created");
        AuditLog orderLog = new AuditLog("Order", 2L, testUser, "CREATE", null, "Order created");
        AuditLog userLog = new AuditLog("User", 3L, testUser, "CREATE", null, "User created");
        AuditLog categoryLog = new AuditLog("Category", 4L, testUser, "CREATE", null, "Category created");
        AuditLog discountLog = new AuditLog("Discount", 5L, testUser, "CREATE", null, "Discount created");

        // Assert
        assertThat(petLog.getEntityType()).isEqualTo("Pet");
        assertThat(orderLog.getEntityType()).isEqualTo("Order");
        assertThat(userLog.getEntityType()).isEqualTo("User");
        assertThat(categoryLog.getEntityType()).isEqualTo("Category");
        assertThat(discountLog.getEntityType()).isEqualTo("Discount");
    }

    @Test
    @DisplayName("Should handle system actions without user")
    void shouldHandleSystemActionsWithoutUser() {
        // Act
        AuditLog systemLog = new AuditLog(
            "System",
            100L,
            null,
            "SYSTEM_CLEANUP",
            "Cleaned up expired sessions",
            "5 sessions removed"
        );

        // Assert
        assertThat(systemLog.getUser()).isNull();
        assertThat(systemLog.getAction()).isEqualTo("SYSTEM_CLEANUP");
        assertThat(systemLog.getEntityType()).isEqualTo("System");
    }

    @Test
    @DisplayName("Should handle complex change descriptions")
    void shouldHandleComplexChangeDescriptions() {
        // Arrange
        String complexOldValue = "{\n  \"name\": \"Old Name\",\n  \"price\": 100.00,\n  \"status\": \"ACTIVE\"\n}";
        String complexNewValue = "{\n  \"name\": \"New Name\",\n  \"price\": 150.00,\n  \"status\": \"INACTIVE\"\n}";

        // Act
        AuditLog complexLog = new AuditLog("Product", 50L, testUser, "UPDATE", complexOldValue, complexNewValue);

        // Assert
        assertThat(complexLog.getOldValue()).contains("Old Name", "100.00", "ACTIVE");
        assertThat(complexLog.getNewValue()).contains("New Name", "150.00", "INACTIVE");
    }
}
