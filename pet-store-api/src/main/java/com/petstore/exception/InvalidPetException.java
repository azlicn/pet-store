package com.petstore.exception;

/**
 * Exception thrown when a pet is invalid
 */
public class InvalidPetException extends RuntimeException {
    
    /**
     * Creates exception with a custom message
     *
     * @param message the custom error message
     */
    public InvalidPetException(String message) {
        super(message);
    }
}
