package com.petstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petstore.model.Delivery;

/**
 * Repository for managing delivery entities in the database
 */
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
