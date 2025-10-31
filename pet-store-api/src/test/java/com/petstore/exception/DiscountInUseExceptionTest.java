package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DiscountInUseException.
 */
class DiscountInUseExceptionTest {

    @Test
    @DisplayName("DiscountInUseException message contains discount ID")
    void shouldCreateWithDiscountId() {
        DiscountInUseException ex = new DiscountInUseException(456L);
        assertThat(ex.getMessage()).contains("456");
        assertThat(ex.getMessage()).containsIgnoringCase("in use");
    }

}
