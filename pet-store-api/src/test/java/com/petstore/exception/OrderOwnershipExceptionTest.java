package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OrderOwnershipException.
 */
class OrderOwnershipExceptionTest {

    /**
     * Test creating exception with order ID and user ID.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with order ID and user ID")
    void shouldCreateWithOrderAndUserId() {
        OrderOwnershipException ex = new OrderOwnershipException(123L, 456L);
        assertThat(ex.getMessage())
                .contains("User with id 456")
                .contains("order with id 123")
                .contains("does not own");
    }

    /**
     * Test creating exception with a custom message.
     */
    @Test
    @DisplayName("Constructor - Should create with custom message")
    void shouldCreateWithCustomMessage() {
        String message = "Custom ownership error";
        OrderOwnershipException ex = new OrderOwnershipException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
