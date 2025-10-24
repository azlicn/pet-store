package com.petstore.repository;

import com.petstore.enums.PetStatus;
import com.petstore.model.Pet;
import com.petstore.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing pet entities in the database
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * Finds pets by their status
     *
     * @param status the pet status to search for
     * @return list of pets with the given status
     */
    List<Pet> findByStatus(PetStatus status);

    /**
     * Finds pets in a specific category
     *
     * @param categoryId the category ID to search for
     * @return list of pets in the given category
     */
    List<Pet> findByCategoryId(Long categoryId);

    /**
     * Finds pets by name (case-insensitive partial match)
     *
     * @param name the pet name to search for
     * @return list of pets with matching names
     */
    @Query("SELECT p FROM Pet p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Pet> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Finds pets available for purchase
     *
     * @param status the required pet status
     * @return list of pets without owners and given status
     */
    List<Pet> findByOwnerIsNullAndStatus(PetStatus status);

    /**
     * Finds pets owned by a specific user
     *
     * @param owner the pet owner
     * @return list of pets owned by the user
     */
    List<Pet> findByOwner(User owner);

    /**
     * Finds pets created by a specific user
     *
     * @param createdBy ID of the user who created the pets
     * @return list of pets created by the user
     */
    List<Pet> findByCreatedBy(Long createdBy);

    /**
     * Finds pets matching multiple filter criteria
     *
     * @param name optional pet name filter
     * @param categoryId optional category ID filter
     * @param status optional pet status filter
     * @param pageable pagination parameters
     * @return filtered list of pets ordered by creation date
     */
    @Query("SELECT p FROM Pet p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:status IS NULL OR p.status = :status) " +
            "ORDER BY p.createdAt DESC")
    List<Pet> findPetsByFilters(@Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("status") PetStatus status,
            Pageable pageable);

    /**
     * Finds the most recently added pets with a specific status
     *
     * @param status the required pet status
     * @param pageable pagination parameters for limiting results
     * @return list of pets ordered by creation date
     */
    @Query("SELECT p FROM Pet p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Pet> findLatestPetsByStatus(@Param("status") PetStatus status, Pageable pageable);
}