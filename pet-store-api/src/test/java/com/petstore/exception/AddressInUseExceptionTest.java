package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AddressInUseException.
 */
class AddressInUseExceptionTest {

    /**
     * Test creating AddressInUseException with an address ID.
     */
    @Test
    @DisplayName("AddressInUseException Tests")
    void shouldCreateWithAddressId() {
        AddressInUseException ex = new AddressInUseException(42L);
        assertThat(ex.getMessage()).contains("42");
        assertThat(ex.getMessage()).contains("associated with existing orders");
    }

    /**
     * Test creating AddressInUseException with a custom message.
     */
    @Test
    @DisplayName("AddressInUseException Custom Message Tests")
    void shouldCreateWithCustomMessage() {
        String msg = "Custom error message";
        AddressInUseException ex = new AddressInUseException(msg, true);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }
}
