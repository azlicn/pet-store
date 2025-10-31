package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AddressNotFoundException.
 */
class AddressNotFoundExceptionTest {

    @Test
    @DisplayName("AddressNotFoundException message contains address ID")
    void shouldCreateWithAddressId() {
        AddressNotFoundException ex = new AddressNotFoundException(123L);
        assertThat(ex.getMessage()).contains("123");
        assertThat(ex.getMessage()).containsIgnoringCase("not found");
    }

    @Test
    @DisplayName("AddressNotFoundException custom message")
    void shouldCreateWithCustomMessage() {
        String msg = "Custom address not found error";
        AddressNotFoundException ex = new AddressNotFoundException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }
}
