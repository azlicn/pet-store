package com.petstore.exception;

/**
 * Exception thrown when user authentication fails (invalid credentials)
 */
public class AuthenticationFailedException extends RuntimeException {
    
    /**
     * Creates exception with authentication error message
     *
     * @param message the authentication error details
     */
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
