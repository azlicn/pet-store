package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PetAlreadySoldException.
 */
class PetAlreadySoldExceptionTest {

    /**
     * Test creating the exception with a pet ID.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with pet ID")
    void shouldCreateWithPetId() {

        PetAlreadySoldException ex = new PetAlreadySoldException(88L);
        assertThat(ex.getMessage()).contains("88");
        assertThat(ex.getMessage()).contains("already been sold");
    }

    /**
     * Test creating the exception with a custom message.
     */
    @Test
    @DisplayName("Constructor - Should create with custom message")
    void shouldCreateWithCustomMessage() {

        String message = "Pet is already sold to another customer.";
        PetAlreadySoldException ex = new PetAlreadySoldException(message);

        assertThat(ex.getMessage()).isEqualTo(message);
    }

}
