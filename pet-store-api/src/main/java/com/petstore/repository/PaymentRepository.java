package com.petstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Payment;

/**
 * Repository for managing payment entities in the database
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
