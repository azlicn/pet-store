package com.petstore.exception;

/**
 * Custom exception thrown when attempting to delete a user who has created or owns pets that still exist in the database
 */
public class UserInUseException extends RuntimeException {
    
    public UserInUseException(String message) {
        super(message);
    }
}