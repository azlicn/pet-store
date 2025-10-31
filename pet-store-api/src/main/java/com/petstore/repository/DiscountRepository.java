package com.petstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Discount;

/**
 * Repository for managing discount entities in the database
 */
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    /**
     * Finds a discount by its code
     *
     * @param code the discount code to search for
     * @return an Optional containing the discount if found, or empty if not
     */
    Optional<Discount> findByCode(String code);

    /**
     * Checks if a discount exists by its code
     *
     * @param code the discount code to check
     * @return true if the discount exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Checks if a discount with the given code exists, excluding a specific ID
     *
     * @param code the discount code to check
     * @param id the ID to exclude from the check
     * @return true if a discount with the code exists excluding the given ID, false otherwise
     */ 
    boolean existsByCodeAndIdNot(String code, Long id);

}
