package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InvalidCategoryException.
 */
class InvalidCategoryExceptionTest {

    /**
     * Test creating InvalidCategoryException with a message.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with category name")
    void shouldCreateWithMessage() {
        String msg = "Invalid category";
        InvalidCategoryException ex = new InvalidCategoryException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }
}
