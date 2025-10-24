package com.petstore.exception;

/**
 * Exception thrown when attempting to delete a category that has pets
 */
public class CategoryInUseException extends RuntimeException {
    
    /**
     * Creates exception with standard category-in-use message
     *
     * @param categoryName name of the category being deleted
     * @param petCount number of pets using this category
     */
    public CategoryInUseException(String categoryName, int petCount) {
        super(String.format("Cannot delete category '%s' because it is being used by %d pet(s)", categoryName, petCount));
    }

    /**
     * Creates exception with a custom message
     *
     * @param message the custom error message
     * @param isCustomMessage flag to differentiate from the category constructor
     */
    public CategoryInUseException(String message, boolean isCustomMessage) {
        super(message);
    }
}