package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidDiscountException.
 */
class InvalidDiscountExceptionTest {

    /**
     * Test creating InvalidDiscountException with a message.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with discount code")
    void shouldCreateWithMessage() {
        String msg = "Invalid discount code";
        InvalidDiscountException ex = new InvalidDiscountException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }
}
