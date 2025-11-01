package com.petstore.exception;

/**
 * Exception thrown when a payment is invalid or cannot be processed.
 */
public class InvalidPaymentException extends RuntimeException {
    
    /**
     * Creates exception with the specified error message.
     *
     * @param message the detail message explaining why the payment is invalid
     */
    public InvalidPaymentException(String message) {
        super(message);
    }
    
}
