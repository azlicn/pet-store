package com.petstore.exception;

/**
 * Exception thrown when an unsupported payment type is encountered
 */
public class UnsupportedPaymentTypeException extends RuntimeException {

    /**
     * Creates exception with standard unsupported payment type message
     *
     * @param message the unsupported payment type
     */
    public UnsupportedPaymentTypeException(String message) {
        super(message);
    }
    
}
