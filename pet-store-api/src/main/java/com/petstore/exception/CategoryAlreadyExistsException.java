package com.petstore.exception;

/**
 * Exception thrown when creating a category with a name that already exists
 */
public class CategoryAlreadyExistsException extends RuntimeException {
    
    /**
     * Creates exception with standard duplicate category message
     *
     * @param categoryName name of the category that already exists
     */
    public CategoryAlreadyExistsException(String categoryName) {
        super(String.format("Category with name '%s' already exists.", categoryName));
    }
    
    /**
     * Creates exception with a custom message
     *
     * @param message the custom error message
     * @param isCustomMessage flag to differentiate from the category constructor
     */
    public CategoryAlreadyExistsException(String message, boolean isCustomMessage) {
        super(message);
    }
}