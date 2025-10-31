package com.petstore.exception;

/**
 * Exception thrown when a requested category is not found in the database
 */
public class CategoryNotFoundException extends RuntimeException {

    /**
     * Creates exception indicating the category with the specified ID was not found
     *
     * @param id the ID of the category that was not found
     */
    public CategoryNotFoundException(Long id) {
        super("Category not found: " + id);
    }
}
