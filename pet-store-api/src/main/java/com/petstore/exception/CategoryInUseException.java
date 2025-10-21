package com.petstore.exception;

/**
 * Custom exception thrown when attempting to delete a category that is still being used by pets
 */
public class CategoryInUseException extends RuntimeException {
    
    public CategoryInUseException(String message) {
        super(message);
    }
}