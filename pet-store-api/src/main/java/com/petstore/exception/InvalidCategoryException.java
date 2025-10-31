package com.petstore.exception;

/**
 * Exception thrown when a category is invalid
 */
public class InvalidCategoryException extends RuntimeException {

    /**
     * Creates exception with a custom message
     *
     * @param message the custom error message
     */
    public InvalidCategoryException(String message) {
        super(message);
    }

}
