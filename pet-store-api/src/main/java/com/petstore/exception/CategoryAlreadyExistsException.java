package com.petstore.exception;

/**
 * Custom exception thrown when attempting to create a category that already exists
 */
public class CategoryAlreadyExistsException extends RuntimeException {
    
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}