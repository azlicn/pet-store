package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DiscountAlreadyExistsException.
 */
class DiscountAlreadyExistsExceptionTest {

    /**
     * Test creating DiscountAlreadyExistsException with a discount code.
     */
    @Test
    @DisplayName("DiscountAlreadyExistsException Tests")
    void shouldCreateWithCode() {
        DiscountAlreadyExistsException ex = new DiscountAlreadyExistsException("SAVE10");
        assertThat(ex.getMessage()).contains("SAVE10");
        assertThat(ex.getMessage()).contains("already exists");
    }
}
