package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CartItemNotFoundException.
 */
class CartItemNotFoundExceptionTest {
    
    /**
     * Test creating CartItemNotFoundException with a cart item ID.
     */
    @Test
    @DisplayName("CartItemNotFoundException Tests")
    void shouldCreateWithCartItemId() {
        CartItemNotFoundException ex = new CartItemNotFoundException(77L);
        assertThat(ex.getMessage()).contains("77");
        assertThat(ex.getMessage()).contains("not found");
    }

}
