package com.petstore.exception;

/**
 * Exception thrown when an address is in use in the system.
 */
public class AddressInUseException extends RuntimeException {

    /**
     * Creates exception with standard address-in-use message
     *
     * @param addressId ID of the address being deleted
     */
    public AddressInUseException(Long addressId) {
        super(String.format("Cannot delete address with ID '%d' because it is associated with existing orders", addressId));
    }

    /**
     * Creates exception with a custom message
     *
     * @param message the custom error message
     * @param isCustomMessage flag to differentiate from the address constructor
     */
    public AddressInUseException(String message, boolean isCustomMessage) {
        super(message);
    }

}
