package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthenticationFailedException Tests")
class AuthenticationFailedExceptionTest {
    
    @Test
    @DisplayName("Constructor - Should create correct message with custom message")
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
        String customMessage = "Invalid credentials";
        AuthenticationFailedException exception = new AuthenticationFailedException(customMessage);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}