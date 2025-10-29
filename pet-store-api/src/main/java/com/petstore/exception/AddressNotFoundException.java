package com.petstore.exception;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(Long addressId) {
        super(String.format("Address not found with id: %d", addressId));
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

}
