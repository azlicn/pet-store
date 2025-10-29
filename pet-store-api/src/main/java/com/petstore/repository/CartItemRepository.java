package com.petstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petstore.model.CartItem;

public interface CartItemRepository  extends JpaRepository<CartItem, Long>{
    Optional<CartItem> findById(Long id);

    boolean existsById(Long id);
}
