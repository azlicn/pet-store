package com.petstore.exception;

/**
 * Exception thrown when a user is invalid
 */
public class InvalidUserException extends RuntimeException {
    
    /**
     * Creates exception with a custom message
     *
     * @param message the custom error message
     */
    public InvalidUserException(String message) {
        super(message);
    }
    
}
