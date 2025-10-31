package com.petstore.exception;

/**
 * Exception thrown when a pet is already sold.
 */
public class PetAlreadySoldException extends RuntimeException {

    /**
     * Constructs a new PetAlreadySoldException with the specified detail message.
     *
     * @param message the detail message explaining why the pet is considered already sold
     */
    public PetAlreadySoldException(String message) {
        super(message);
    }

    /**
     * Constructs a new PetAlreadySoldException with a standard message.
     *
     * @param petName the name of the pet that is already sold
     */
    public PetAlreadySoldException(Long petId) {
        super(String.format("Pet with ID '%d' has already been sold.", petId));
    }

}
