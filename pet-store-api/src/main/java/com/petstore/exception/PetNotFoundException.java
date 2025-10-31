package com.petstore.exception;

/**
 * Exception thrown when a pet is not found in the system.
 */
public class PetNotFoundException extends RuntimeException {

    /**
     * Creates exception with standard pet not found message
     *
     * @param message the custom error message
     */
    public PetNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates exception with standard pet not found message
     *
     * @param petId ID of the pet that was not found
     */
    public PetNotFoundException(Long petId) {
        super(String.format("Pet not found with ID '%d'", petId));
    }
    
}
