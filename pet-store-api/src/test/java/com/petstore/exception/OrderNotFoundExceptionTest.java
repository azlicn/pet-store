package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OrderNotFoundException.
 */
class OrderNotFoundExceptionTest {

    /**
     * Test creating OrderNotFoundException with an order ID.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with order ID")
    void shouldCreateWithOrderId() {
        OrderNotFoundException ex = new OrderNotFoundException(123L);
        assertThat(ex.getMessage()).contains("123");
        assertThat(ex.getMessage()).contains("not found");
    }
}
