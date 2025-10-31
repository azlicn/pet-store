package com.petstore.exception;

/**
 * Exception thrown when an address is not found in the system.
 */
public class AddressNotFoundException extends RuntimeException {


    /**
     * Constructs a new AddressNotFoundException with the specified address ID.
     *
     * @param addressId the ID of the address that was not found
     */
    public AddressNotFoundException(Long addressId) {
        super(String.format("Address not found with id: %d", addressId));
    }

    /**
     * Constructs a new AddressNotFoundException with a custom message.
     *
     * @param message the detail message
     */
    public AddressNotFoundException(String message) {
        super(message);
    }

}
