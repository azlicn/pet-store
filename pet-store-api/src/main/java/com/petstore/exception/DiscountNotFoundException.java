package com.petstore.exception;

/**
 * Exception thrown when a requested discount is not found in the database
 */
public class DiscountNotFoundException extends RuntimeException {

    /**
     * Creates exception indicating the discount with the specified ID was not found
     *
     * @param id the ID of the discount that was not found
     */
    public DiscountNotFoundException(Long id) { 
        super("Discount not found with ID: " + id);
    }

    /**
     * Creates exception indicating the discount with the specified code was not found
     *
     * @param code the code of the discount that was not found
     */
    public DiscountNotFoundException(String code) {
        super("Discount not found: " + code);
    }
}
