package com.petstore.exception;

/**
 * Custom exception thrown when attempting to delete a user who has created or owns pets that still exist in the database.
 * 
 */
public class UserInUseException extends RuntimeException {

    /**
     * Constructs a new UserInUseException with the specified detail message.
     *
     * @param message the detail message explaining why the user cannot be deleted,
     *               typically includes information about the number of pets owned
     */
    public UserInUseException(String message) {
        super(message);
    }
}