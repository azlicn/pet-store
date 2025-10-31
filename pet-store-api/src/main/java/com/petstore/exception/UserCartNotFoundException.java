package com.petstore.exception;

/**
 * Exception thrown when a user's cart is not found in the system.
 */
public class UserCartNotFoundException extends RuntimeException {

    /**
     * Creates exception with standard user cart not found message
     *
     * @param userId ID of the user whose cart was not found
     */
    public UserCartNotFoundException(Long userId) {
        super(String.format("Cart not found for user with ID '%d'", userId));
    }
    
}
