package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PetAlreadyExistInUserCartException.
 */
class PetAlreadyExistInUserCartExceptionTest {

    /**
     * Test constructor with pet ID.
     */
    @Test
    @DisplayName("PetAlreadyExistInUserCartException message contains pet ID")
    void shouldCreateWithPetId() {
        PetAlreadyExistInUserCartException ex = new PetAlreadyExistInUserCartException(789L);
        assertThat(ex.getMessage()).contains("789");
        assertThat(ex.getMessage()).containsIgnoringCase("already in the user's cart");
    }

    /**
     * Test constructor with custom message.
     */
    @Test
    @DisplayName("PetAlreadyExistInUserCartException custom message")
    void shouldCreateWithCustomMessage() {
        String msg = "Custom pet already in cart error";
        PetAlreadyExistInUserCartException ex = new PetAlreadyExistInUserCartException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }
}
