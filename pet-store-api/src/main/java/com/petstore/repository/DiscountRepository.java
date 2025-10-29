package com.petstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long>  {

    Optional<Discount> findByCode(String code);

    boolean existsByCode(String code);
    
}
