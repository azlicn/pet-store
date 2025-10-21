package com.petstore.repository;

import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
import com.petstore.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByStatus(PetStatus status);

    List<Pet> findByCategoryId(Long categoryId);

    // Custom query for name search that works better with H2 database
    @Query("SELECT p FROM Pet p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Pet> findByNameContainingIgnoreCase(@Param("name") String name);

    // Find pets available for purchase (no owner and available status)
    List<Pet> findByOwnerIsNullAndStatus(PetStatus status);

    // Find pets owned by a specific user
    List<Pet> findByOwner(User owner);

    // Find pets created by a specific user
    List<Pet> findByCreatedBy(Long createdBy);

    @Query("SELECT p FROM Pet p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:status IS NULL OR p.status = :status) " +
            "ORDER BY p.createdAt DESC")
    List<Pet> findPetsByFilters(@Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("status") PetStatus status,
            Pageable pageable);

    // Method specifically for getting latest pets with limit
    @Query("SELECT p FROM Pet p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Pet> findLatestPetsByStatus(@Param("status") PetStatus status, Pageable pageable);
}