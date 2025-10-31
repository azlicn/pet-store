package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidUserException.
 */
class InvalidUserExceptionTest {

    /**
     * Test creating InvalidUserException with a message.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with user details")
    void shouldCreateWithMessage() {
        String msg = "Invalid user";
        InvalidUserException ex = new InvalidUserException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }
}
