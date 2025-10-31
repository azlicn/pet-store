package com.petstore.exception;

/**
 * Exception thrown when attempting to access a cart that is empty
 */
public class CartEmptyException extends RuntimeException {

    /**
     * Creates exception indicating the cart is empty for the specified user
     *
     * @param userId ID of the user whose cart is empty
     */
    public CartEmptyException(Long userId) {
        super("Cart is empty for user with ID: " + userId);
    }
}
