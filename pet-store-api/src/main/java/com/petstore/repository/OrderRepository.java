package com.petstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Order;
import com.petstore.model.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for managing order entities in the database
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds orders by user ID
     *
     * @param userId the user ID to search for
     * @return list of orders belonging to the given user
     */
    List<Order> findByUserId(Long userId);

    /**
     * Checks if an address is used as shipping or billing address in any order
     *
     * @param address the address to check
     * @return true if the address is used, false otherwise
     */
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.shippingAddress = :address OR o.billingAddress = :address")
    boolean existsByAddressUsed(@Param("address") Address address);

    /**
     * Finds an order by its ID and user ID
     *
     * @param orderId the order ID to search for
     * @param userId the user ID to search for
     * @return an Optional containing the order if found, or empty if not
     */
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

}
