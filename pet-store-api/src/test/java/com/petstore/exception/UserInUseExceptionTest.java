package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserInUseException.
 */
@DisplayName("UserInUseException Tests")
class UserInUseExceptionTest {

    /**
     * Test creating the exception with a custom message.
     */
    @Test
    @DisplayName("Constructor - Should use custom message when provided")
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
        String customMessage = "Custom error message";
        UserInUseException exception = new UserInUseException(customMessage);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}