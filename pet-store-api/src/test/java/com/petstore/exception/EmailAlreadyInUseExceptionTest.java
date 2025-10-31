package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmailAlreadyInUseException.
 */
@DisplayName("EmailAlreadyInUseException Tests")
class EmailAlreadyInUseExceptionTest {

    /**
     * Test creating EmailAlreadyInUseException with an email.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with email")
    void constructor_WithEmail_ShouldCreateCorrectMessage() {
        String email = "user@example.com";
        String expectedMessage = String.format("Email '%s' is already in use", email);
        EmailAlreadyInUseException exception = new EmailAlreadyInUseException(email);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    /**
     * Test creating EmailAlreadyInUseException with a custom message.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with custom message")
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
        String customMessage = "Custom error message";
        EmailAlreadyInUseException exception = new EmailAlreadyInUseException(customMessage, true);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}