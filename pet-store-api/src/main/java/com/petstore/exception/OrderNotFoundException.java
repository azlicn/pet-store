package com.petstore.exception;

/**
 * Exception thrown when an order is not found in the system.
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a default message for the given order ID.
     *
     * @param orderId the ID of the order that was not found
     */
    public OrderNotFoundException(Long orderId) {
        super(String.format("Order with id %d not found.", orderId));
    }

    /**
     * Constructs a new exception with a default message for the given order ID.
     *
     * @param orderId the ID of the order that was not found
     */
    public OrderNotFoundException(Long orderId, Long userId) {
        super(String.format("Order with id %d not found for user %d.", orderId, userId));
    }

    /**
     * Constructs a new exception with a custom message.
     *
     * @param message the detail message
     */
    public OrderNotFoundException(String message) {
        super(message);
    }
    
}
