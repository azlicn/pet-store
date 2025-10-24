package com.petstore.exception;

/**
 * Custom exception thrown when attempting to delete a user who has created or owns pets that still exist in the database.
 * 
 * <p>This runtime exception is thrown during user deletion operations to prevent orphaned pet records
 * and maintain referential integrity in the database. The user must first transfer ownership or delete
 * their pets before they can be removed from the system.</p>
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