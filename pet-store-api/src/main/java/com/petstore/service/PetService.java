package com.petstore.service;

import com.petstore.model.Pet;
import com.petstore.enums.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.User;
import com.petstore.repository.PetRepository;
import com.petstore.repository.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

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
     * Retrieves all pets in the store
     *
     * @return list of all pets
     */
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    /**
     * Finds pets owned by a user
     *
     * @param owner the pet owner
     * @return list of pets owned by the user
     */
    public List<Pet> getPetsByOwner(User owner) {
        return petRepository.findByOwner(owner);
    }

    /**
     * Finds pets created by a user
     *
     * @param createdBy ID of the user who created the pets
     * @return list of pets created by the user
     */
    public List<Pet> getPetsByCreator(Long createdBy) {
        return petRepository.findByCreatedBy(createdBy);
    }

    /**
     * Finds all pets associated with a user (both owned and created)
     *
     * @param user the user to search for
     * @return list of pets ordered by creation date (newest first)
     */
    public List<Pet> getPetsByUser(User user) {
        List<Pet> ownedPets = petRepository.findByOwner(user);
        List<Pet> createdPets = petRepository.findByCreatedBy(user.getId());

        // Combine both lists and remove duplicates (in case a user owns a pet they
        // created)
        Set<Pet> allPets = new HashSet<>(ownedPets);
        allPets.addAll(createdPets);

        // Convert back to list and sort by creation date (newest first)
        List<Pet> result = new ArrayList<>(allPets);
        result.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        return result;
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
     * Finds pets by their status
     *
     * @param status the pet status to search for
     * @return list of pets with the given status
     */
    public List<Pet> getPetsByStatus(PetStatus status) {
        return petRepository.findByStatus(status);
    }

    /**
     * Searches pets using multiple filters
     *
     * @param name optional pet name filter
     * @param categoryId optional category filter
     * @param status optional status filter
     * @param limit optional result size limit
     * @return filtered list of pets
     */
    public List<Pet> findPetsByFilters(String name, Long categoryId, PetStatus status, Integer limit) {
        Pageable pageable = limit != null ? PageRequest.of(0, limit) : Pageable.unpaged();
        return petRepository.findPetsByFilters(name, categoryId, status, pageable);
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
     * Creates a new pet
     *
     * @param pet the pet details to save
     * @return the created pet
     */
    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    /**
     * Processes a pet purchase
     *
     * @param petId ID of the pet to purchase
     * @param buyer the user buying the pet
     * @return the updated pet, or null if purchase not possible
     */
    public Pet purchasePet(Long petId, User buyer) {
        Optional<Pet> existingPet = petRepository.findById(petId);

        if (existingPet.isPresent()) {
            Pet pet = existingPet.get();

            // Only allow purchase if pet is available and not owned
            if (pet.getStatus() == PetStatus.AVAILABLE && pet.getOwner() == null) {
                pet.setOwner(buyer);
                pet.setStatus(PetStatus.SOLD);
                return petRepository.save(pet);
            }
        }

        return null;
    }

    /**
     * Updates an existing pet
     *
     * @param id the pet ID to update
     * @param petDetails the new pet details
     * @return the updated pet, or null if not found
     */
    public Pet updatePet(Long id, Pet petDetails) {
        Optional<Pet> existingPet = petRepository.findById(id);

        if (existingPet.isPresent()) {
            Pet pet = existingPet.get();
            pet.setName(petDetails.getName());
            pet.setDescription(petDetails.getDescription());

            // Handle category properly by looking it up by ID
            if (petDetails.getCategory() != null && petDetails.getCategory().getId() != null) {
                Optional<Category> category = categoryRepository.findById(petDetails.getCategory().getId());
                if (category.isPresent()) {
                    pet.setCategory(category.get());
                }
            }

            pet.setPrice(petDetails.getPrice());
            pet.setStatus(petDetails.getStatus());
            pet.setOwner(petDetails.getOwner());
            pet.setPhotoUrls(petDetails.getPhotoUrls());
            pet.setTags(petDetails.getTags());

            return petRepository.save(pet);
        }

        return null;
    }

    /**
     * Deletes a pet
     *
     * @param id the ID of the pet to delete
     * @return true if deleted, false if not found
     */
    public Boolean deletePet(Long id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Updates a pet's status
     *
     * @param id the pet ID to update
     * @param status the new status to set
     * @return the updated pet, or null if not found
     */
    public Pet updatePetStatus(Long id, PetStatus status) {
        Optional<Pet> existingPet = petRepository.findById(id);

        if (existingPet.isPresent()) {
            Pet pet = existingPet.get();
            pet.setStatus(status);
            return petRepository.save(pet);
        }

        return null;
    }
}