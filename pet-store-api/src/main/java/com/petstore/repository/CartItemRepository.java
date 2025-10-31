package com.petstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petstore.model.CartItem;

/**
 * Repository for managing cart item entities in the database
 */
public interface CartItemRepository  extends JpaRepository<CartItem, Long>{
    
    /**
     * Finds a cart item by its ID
     *
     * @param id the cart item ID to search for
     * @return an Optional containing the cart item if found, or empty if not
     */
    Optional<CartItem> findById(Long id);

    /**
     * Checks if a cart item exists by its ID
     *
     * @param id the cart item ID to check
     * @return true if the cart item exists, false otherwise
     */
    boolean existsById(Long id);
}
