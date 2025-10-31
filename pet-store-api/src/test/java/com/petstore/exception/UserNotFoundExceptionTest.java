package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserNotFoundException.
 */
@DisplayName("UserNotFoundException Tests")
class UserNotFoundExceptionTest {

    /**
     * Test creating the exception with a user ID.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with user ID")
    void constructor_WithUserId_ShouldCreateCorrectMessage() {
        Long userId = 123L;
        String expectedMessage = String.format("User not found with id: %d", userId);
        UserNotFoundException exception = new UserNotFoundException(userId);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    /**
     * Test creating the exception with an email.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with email")
    void constructor_WithEmail_ShouldCreateCorrectMessage() {
        String email = "user@example.com";
        String expectedMessage = String.format("User not found with email: '%s'", email);
        UserNotFoundException exception = new UserNotFoundException(email);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    /**
     * Test creating the exception with a custom message.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with custom message")
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
        String customMessage = "Custom error message";
        UserNotFoundException exception = new UserNotFoundException(customMessage, true);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}