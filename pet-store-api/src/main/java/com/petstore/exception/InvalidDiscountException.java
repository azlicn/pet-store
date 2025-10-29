package com.petstore.exception;

/**
 * Exception thrown when a discount code is invalid or expired.
 */
public class InvalidDiscountException extends RuntimeException {
    /**
     * Constructs a new exception with a default message.
     */
    public InvalidDiscountException() {
        super("Invalid or expired discount code");
    }

    /**
     * Constructs a new exception with a custom message.
     *
     * @param message the detail message
     */
    public InvalidDiscountException(String message) {
        super(message);
    }
}
