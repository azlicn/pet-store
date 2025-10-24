package com.petstore.repository;

import com.petstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing pet categories in the database
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Finds a category by its exact name (trimmed, case-sensitive)
     *
     * @param name the category name to search for
     * @return the matching category if found
     */
    @Query("SELECT c FROM Category c WHERE TRIM(c.name) = TRIM(:name)")
    Optional<Category> findByName(@Param("name") String name);
    
    /**
     * Checks if a category exists with the given name
     *
     * @param name the category name to check
     * @return true if the category exists
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE TRIM(c.name) = TRIM(:name)")
    boolean existsByName(@Param("name") String name);
}