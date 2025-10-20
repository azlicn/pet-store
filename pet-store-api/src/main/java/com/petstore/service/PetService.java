package com.petstore.service;

import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.User;
import com.petstore.repository.PetRepository;
import com.petstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    // Get pets available for purchase (store inventory - no owner)
    public List<Pet> getAvailablePets() {
        return petRepository.findByOwnerIsNullAndStatus(PetStatus.AVAILABLE);
    }

    // Get pets owned by a specific user
    public List<Pet> getPetsByOwner(User owner) {
        return petRepository.findByOwner(owner);
    }

    // Get pets created by a specific user
    public List<Pet> getPetsByCreator(Long createdBy) {
        return petRepository.findByCreatedBy(createdBy);
    }

    // Get pets associated with a user (both owned and created)
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

    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    public List<Pet> getPetsByStatus(PetStatus status) {
        return petRepository.findByStatus(status);
    }

    public List<Pet> getPetsByCategory(Long categoryId) {
        return petRepository.findByCategoryId(categoryId);
    }

    public List<Pet> searchPetsByName(String name) {
        return petRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Pet> findPetsByFilters(String name, Long categoryId, PetStatus status, Integer limit) {
        Pageable pageable = limit != null ? PageRequest.of(0, limit) : Pageable.unpaged();
        return petRepository.findPetsByFilters(name, categoryId, status, pageable);
    }

    // Method specifically for getting latest pets (useful for home page)
    public List<Pet> getLatestAvailablePets(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return petRepository.findLatestPetsByStatus(PetStatus.AVAILABLE, pageable);
    }

    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    // Purchase a pet - assign owner and change status to SOLD
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

    public Pet updatePet(Long id, Pet petDetails) {
        Optional<Pet> existingPet = petRepository.findById(id);

        if (existingPet.isPresent()) {
            Pet pet = existingPet.get();
            pet.setName(petDetails.getName());

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

    public boolean deletePet(Long id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
            return true;
        }
        return false;
    }

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