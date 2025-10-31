package com.petstore.exception;

/**
 * Exception thrown when a cart item is not found in the system.
 */
public class CartItemNotFoundException extends RuntimeException {
    
    /**
     * Creates exception with standard cart item not found message
     *
     * @param cartItemId ID of the cart item that was not found
     */
    public CartItemNotFoundException(Long cartItemId) {
        super(String.format("Cart item with ID '%d' not found", cartItemId));
    }

}
