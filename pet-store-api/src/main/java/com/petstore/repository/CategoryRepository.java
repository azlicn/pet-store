package com.petstore.repository;

import com.petstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @Query("SELECT c FROM Category c WHERE TRIM(c.name) = TRIM(:name)")
    Optional<Category> findByName(@Param("name") String name);
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE TRIM(c.name) = TRIM(:name)")
    boolean existsByName(@Param("name") String name);
}