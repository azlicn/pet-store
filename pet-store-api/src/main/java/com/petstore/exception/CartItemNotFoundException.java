package com.petstore.exception;

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
