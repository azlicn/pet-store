package com.petstore.exception;

/**
 * Exception thrown when attempting to delete a discount that is currently in use
 */
public class DiscountInUseException extends RuntimeException {

    /**
     * Creates exception indicating the discount with the specified ID is in use
     *
     * @param id the ID of the discount that is in use
     */
    public DiscountInUseException(Long id) {
        super("Discount is currently in use and cannot be deleted: " + id);
    }

}
