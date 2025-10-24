package com.petstore.exception;

/**
 * Exception thrown when attempting to register with an email that's already taken
 */
public class EmailAlreadyInUseException extends RuntimeException {
    /**
     * Creates exception with standard email-in-use message
     * 
     * @param email the email address that is already registered
     */
    public EmailAlreadyInUseException(String email) {
        super(String.format("Email '%s' is already in use", email));
    }

    /**
     * Creates exception with a custom message
     * 
     * @param message the custom error message
     * @param isCustomMessage flag to differentiate from the email constructor
     */
    public EmailAlreadyInUseException(String message, boolean isCustomMessage) {
        super(message);
    }
}
