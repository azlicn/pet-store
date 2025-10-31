package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserCartNotFoundException.
 */
class UserCartNotFoundExceptionTest {

    /**
     * Test creating the exception with a user ID.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with user ID")
    void shouldCreateWithUserId() {
        UserCartNotFoundException ex = new UserCartNotFoundException(55L);
        assertThat(ex.getMessage()).contains("55");
        assertThat(ex.getMessage()).contains("Cart not found");
    }
}
