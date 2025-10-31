package com.petstore.service;

import com.petstore.model.Pet;
import com.petstore.enums.PetStatus;
import com.petstore.exception.InvalidPetException;
import com.petstore.exception.PetNotFoundException;
import com.petstore.exception.CategoryNotFoundException;
import com.petstore.model.Category;
import com.petstore.repository.PetRepository;
import com.petstore.repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing pets in the store
 */
@Service
public class PetService {

    private final PetRepository petRepository;

    private final CategoryRepository categoryRepository;

    public PetService(PetRepository petRepository, CategoryRepository categoryRepository) {
        this.petRepository = petRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Searches pets using multiple filters with pagination
     *
     * @param name       optional pet name filter
     * @param categoryId optional category filter
     * @param status     optional status filter
     * @param page       page number (zero-based)
     * @param size       page size
     * @return paginated result of pets
     */
    public Page<Pet> findPetsByFiltersPaginated(String name, Long categoryId, PetStatus status, Long userId, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        return petRepository.findPetsByFiltersPaginated(name, categoryId, status, userId, pageable);
    }

    /**
     * Retrieves a pet by its ID
     *
     * @param id the pet ID
     * @return the pet if found
     */
    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    /**
     * Gets the most recently added available pets
     *
     * @param limit maximum number of pets to return
     * @return list of available pets ordered by creation date
     */
    public List<Pet> getLatestAvailablePets(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return petRepository.findLatestPetsByStatus(PetStatus.AVAILABLE, pageable);
    }

    /**
     * Creates a new pet. Returns null if input is null.
     *
     * @param pet the pet details to save
     * @return the created pet, or null if input is null
     */
    public Pet savePet(Pet pet) {
        
        if (pet == null) {
            throw new InvalidPetException("Pet cannot be null");
        }
        return petRepository.save(pet);
    }

    /**
     * Updates an existing pet
     *
     * @param id         the pet ID to update
     * @param petDetails the new pet details
     * @return the updated pet, or null if not found
     */
    public Pet updatePet(Long id, Pet petDetails) {

        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(id));

        // Update allowed fields
        existingPet.setName(petDetails.getName());
        existingPet.setDescription(petDetails.getDescription());
        existingPet.setPrice(petDetails.getPrice());
        existingPet.setStatus(petDetails.getStatus());
        existingPet.setPhotoUrls(petDetails.getPhotoUrls());
        existingPet.setTags(petDetails.getTags());

        // Validate and set category
        if (petDetails.getCategory() != null && petDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(petDetails.getCategory().getId())
                    .orElseThrow(() -> new CategoryNotFoundException(petDetails.getCategory().getId()));
            existingPet.setCategory(category);
        }

        // Optional: only admins can reassign ownership
        if (petDetails.getOwner() != null) {
            existingPet.setOwner(petDetails.getOwner());
        }

        return petRepository.save(existingPet);
    }

    /**
     * Deletes a pet
     *
     * @param id the ID of the pet to delete
     * @return true if deleted, false if not found
     */
    public Boolean deletePet(Long id) {

        if (id == null) {
            throw new InvalidPetException("Pet ID cannot be null");
        }

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(id));

        petRepository.delete(pet);
        return true;
    }

    /**
     * Updates a pet's status
     *
     * @param id     the pet ID to update
     * @param status the new status to set
     * @return the updated pet, or null if not found
     */
    public Pet updatePetStatus(Long id, PetStatus status) {
        Optional<Pet> existingPet = petRepository.findById(id);

        if (existingPet.isPresent()) {
            if (status == null) {
                throw new InvalidPetException("Pet status cannot be null");
            }
            Pet pet = existingPet.get();
            pet.setStatus(status);
            return petRepository.save(pet);
        }

        return null;
    }
}