package com.petstore.exception;

/**
 * Exception thrown when a pet already exists in the user's cart.
 */
public class PetAlreadyExistInUserCartException extends RuntimeException {

    /**
     * Constructs a new PetAlreadyExistInUserCartException with the specified detail message.
     *
     * @param message the detail message explaining why the pet cannot be added to the cart
     */
    public PetAlreadyExistInUserCartException(String message) {
        super(message);
    }

    /**
     * Constructs a new PetAlreadyExistInUserCartException with a standard message.
     *
     * @param petId the ID of the pet that is already in the user's cart
     */
    public PetAlreadyExistInUserCartException(Long petId) {
        super(String.format("Pet with ID '%d' is already in the user's cart.", petId));
    }
    
}
