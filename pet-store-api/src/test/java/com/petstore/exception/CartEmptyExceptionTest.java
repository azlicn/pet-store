package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CartEmptyException.
 */
class CartEmptyExceptionTest {

    /**
     * Test constructor with user ID.
     */
    @Test
    @DisplayName("CartEmptyException message contains user ID and 'cart is empty'")
    void shouldCreateWithUserId() {
        CartEmptyException ex = new CartEmptyException(123L);
        assertThat(ex.getMessage()).contains("123");
        assertThat(ex.getMessage()).containsIgnoringCase("cart is empty");
    }
}
