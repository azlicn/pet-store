package com.petstore.exception;

/**
 * Exception thrown when an unsupported payment type is encountered
 */
public class UnsupportedPaymentException extends RuntimeException {
    
    /**
     * Creates exception with standard unsupported payment message
     *
     * @param message the unsupported payment type
     */
    public UnsupportedPaymentException(String message) {
        super(message);
    }
    
}
