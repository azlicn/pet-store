
package com.petstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.petstore.model.Cart;

/**
 * Repository for managing cart entities in the database
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a cart by user ID
     *
     * @param userId the user ID to search for
     * @return an Optional containing the cart if found, or empty if not
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Finds a cart by user ID, including items and pets (eager fetch)
     *
     * @param userId the user ID to search for
     * @return an Optional containing the cart with items and pets if found, or empty if not
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.pet WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItemsAndPets(@Param("userId") Long userId);

}
