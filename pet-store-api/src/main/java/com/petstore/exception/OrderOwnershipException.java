package com.petstore.exception;

/**
 * Exception thrown when a user tries to access or modify an order they do not own.
 */
public class OrderOwnershipException extends RuntimeException {

    /**
     * Constructs a new exception with a default message for the given order ID and user ID.
     *
     * @param orderId the ID of the order
     * @param userId the ID of the user attempting access
     */
    public OrderOwnershipException(Long orderId, Long userId) {
        super(String.format("User with id %d does not own order with id %d.", userId, orderId));
    }

    /**
     * Constructs a new exception with a custom message.
     *
     * @param message the detail message
     */
    public OrderOwnershipException(String message) {
        super(message);
    }
}
