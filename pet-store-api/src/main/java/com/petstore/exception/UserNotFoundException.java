package com.petstore.exception;

/**
 * Exception thrown when a user cannot be found in the system.
 * This can occur when searching by user ID or email address.
 * 
 */
public class UserNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new UserNotFoundException when a user cannot be found by their ID.
     *
     * @param userId the ID of the user that could not be found
     */
    public UserNotFoundException(Long userId) {
        super(String.format("User not found with id: %d", userId));
    }

    /**
     * Constructs a new UserNotFoundException when a user cannot be found by their email address.
     *
     * @param email the email address of the user that could not be found
     */
    public UserNotFoundException(String email) {
        super(String.format("User not found with email: '%s'", email));
    }

    /**
     * Constructs a new UserNotFoundException with a custom message.
     *
     * @param message the custom error message
     * @param isCustomMessage flag to indicate this is a custom message constructor (to differentiate from email constructor)
     */
    public UserNotFoundException(String message, boolean isCustomMessage) {
        super(message);
    }
}
