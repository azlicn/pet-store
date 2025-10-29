package com.petstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
