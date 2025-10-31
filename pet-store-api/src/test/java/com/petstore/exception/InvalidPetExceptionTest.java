package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidPetException.
 */
class InvalidPetExceptionTest {

    /**
     * Test creating InvalidPetException with a message.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with pet details")
    void shouldCreateWithMessage() {
        String msg = "Invalid pet details";
        InvalidPetException ex = new InvalidPetException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }
}
