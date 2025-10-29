package com.petstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Order;
import com.petstore.model.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.shippingAddress = :address OR o.billingAddress = :address")
    boolean existsByAddressUsed(@Param("address") Address address);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

}
