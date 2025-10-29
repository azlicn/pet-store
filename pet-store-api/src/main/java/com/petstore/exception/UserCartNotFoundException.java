package com.petstore.exception;

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
