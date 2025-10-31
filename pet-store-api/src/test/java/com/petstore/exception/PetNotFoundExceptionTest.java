package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PetNotFoundException.
 */
class PetNotFoundExceptionTest {

    /**
     * Test creating the exception with a pet ID.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with pet ID")
    void shouldCreateWithPetId() {
        PetNotFoundException ex = new PetNotFoundException(99L);
        assertThat(ex.getMessage()).contains("99");
        assertThat(ex.getMessage()).contains("not found");
    }
}
