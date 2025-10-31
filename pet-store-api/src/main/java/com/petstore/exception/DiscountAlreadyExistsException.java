package com.petstore.exception;

/**
 * Exception thrown when a discount is already exists in the system.
 */
public class DiscountAlreadyExistsException extends RuntimeException {


    /**
     * Creates exception with standard duplicate category message
     *
     * @param discountCode name of the discount that already exists
     */
    public DiscountAlreadyExistsException(String discountCode) {
        super(String.format("Discount with code '%s' already exists", discountCode));
    }
    
    /**
     * Creates exception with a custom message
     *
     * @param message the custom error message
     * @param isCustomMessage flag to differentiate from the discount constructor
     */
    public DiscountAlreadyExistsException(String message, boolean isCustomMessage) {
        super(message);
    }
}
