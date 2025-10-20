package com.petstore.controller;

import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
import com.petstore.model.User;
import com.petstore.model.Role;
import com.petstore.service.PetService;
import com.petstore.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pawfect Store", description = "Pawfect Store API")
public class PetController {
    
    private static final Logger logger = LoggerFactory.getLogger(PetController.class);
    
    @Autowired
    private PetService petService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    @Operation(summary = "Get available pets", description = "Retrieve pets available for purchase (public access)")
    public ResponseEntity<List<Pet>> getAllPets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) PetStatus status,
            @RequestParam(required = false) Integer limit) {
        
        logger.info("Received request to get available pets with filters - name: '{}', categoryId: {}, status: {}, limit: {}", 
                   name, categoryId, status, limit);
        
        try {
            if (name != null || categoryId != null || status != null || limit != null) {
                logger.debug("Applying filters to pet search");
                List<Pet> pets = petService.findPetsByFilters(name, categoryId, status, limit);
                logger.info("Found {} pets matching filter criteria", pets.size());
                return ResponseEntity.ok(pets);
            }
            
            logger.debug("No filters provided, returning all available pets");
            List<Pet> pets = petService.getAllPets();
            logger.info("Retrieved {} available pets from store inventory", pets.size());
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving pets with filters - name: '{}', categoryId: {}, status: {}, limit: {}", 
                        name, categoryId, status, limit, e);
            throw e;
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest available pets", description = "Retrieve the latest available pets (useful for home page)")
    public ResponseEntity<List<Pet>> getLatestPets(
            @RequestParam(defaultValue = "6") int limit) {
        List<Pet> pets = petService.getLatestAvailablePets(limit);
        return ResponseEntity.ok(pets);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Find pet by ID", description = "Returns a single pet")
    public ResponseEntity<Pet> getPetById(
            @Parameter(description = "ID of pet to return") @PathVariable Long id) {
        
        logger.info("Received request to get pet by ID: {}", id);
        
        try {
            Optional<Pet> pet = petService.getPetById(id);
            if (pet.isPresent()) {
                logger.info("Pet found with ID: {} - '{}'", id, pet.get().getName());
                return ResponseEntity.ok(pet.get());
            } else {
                logger.warn("Pet not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving pet with ID: {}", id, e);
            throw e;
        }
    }
    
    @GetMapping("/findByStatus")
    @Operation(summary = "Finds pets by status", description = "Multiple status values can be provided")
    public ResponseEntity<List<Pet>> findPetsByStatus(
            @Parameter(description = "Status values that need to be considered for filter") 
            @RequestParam PetStatus status) {
        List<Pet> pets = petService.getPetsByStatus(status);
        return ResponseEntity.ok(pets);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Add a new pet", description = "Add a new pet to the store")
    public ResponseEntity<Pet> addPet(@Valid @RequestBody Pet pet) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        logger.info("User '{}' attempting to add new pet: '{}' in category: '{}' with price: ${}", 
                   username, pet.getName(), 
                   pet.getCategory() != null ? pet.getCategory().getName() : "unknown",
                   pet.getPrice());
        
        try {
            Pet savedPet = petService.savePet(pet);
            logger.info("Successfully added new pet with ID: {} - '{}' by user: '{}'", 
                       savedPet.getId(), savedPet.getName(), username);
            return ResponseEntity.ok(savedPet);
        } catch (Exception e) {
            logger.error("Error occurred while adding new pet '{}' by user: '{}'", 
                        pet.getName(), username, e);
            throw e;
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update an existing pet", description = "Update an existing pet by Id")
    public ResponseEntity<Pet> updatePet(
            @Parameter(description = "ID of pet to update") @PathVariable Long id,
            @Valid @RequestBody Pet petDetails) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        logger.info("User '{}' attempting to update pet with ID: {}", username, id);
        
        try {
            Optional<User> currentUserOpt = userRepository.findByEmail(username);
            if (!currentUserOpt.isPresent()) {
                logger.warn("User '{}' not found in database during pet update attempt for ID: {}", username, id);
                return ResponseEntity.status(401).build(); // Unauthorized
            }
            
            User currentUser = currentUserOpt.get();
            
            Optional<Pet> existingPetOpt = petService.getPetById(id);
            if (!existingPetOpt.isPresent()) {
                logger.warn("Pet with ID: {} not found during update attempt by user: '{}'", id, username);
                return ResponseEntity.notFound().build();
            }
            
            Pet existingPet = existingPetOpt.get();
            
            boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
            boolean isOwner = existingPet.getCreatedBy() != null && 
                             existingPet.getCreatedBy().equals(currentUser.getId());
            
            logger.debug("User '{}' permission check for pet ID: {} - isAdmin: {}, isOwner: {}", 
                        username, id, isAdmin, isOwner);
            
            if (!isAdmin && !isOwner) {
                logger.warn("User '{}' denied access to update pet ID: {} - insufficient permissions", username, id);
                return ResponseEntity.status(403).build(); // Forbidden
            }
            
            Pet updatedPet = petService.updatePet(id, petDetails);
            if (updatedPet != null) {
                logger.info("Successfully updated pet ID: {} - '{}' by user: '{}'", 
                           updatedPet.getId(), updatedPet.getName(), username);
                return ResponseEntity.ok(updatedPet);
            } else {
                logger.error("Failed to update pet ID: {} by user: '{}' - service returned null", id, username);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating pet ID: {} by user: '{}'", id, username, e);
            throw e;
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletes a pet", description = "Delete a pet by ID")
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "Pet id to delete") @PathVariable Long id) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        logger.info("Admin user '{}' attempting to delete pet with ID: {}", username, id);
        
        try {
            Optional<Pet> petToDelete = petService.getPetById(id);
            
            boolean deleted = petService.deletePet(id);
            if (deleted) {
                String petName = petToDelete.isPresent() ? petToDelete.get().getName() : "unknown";
                logger.info("Successfully deleted pet ID: {} - '{}' by admin: '{}'", id, petName, username);
                return ResponseEntity.ok().build();
            } else {
                logger.warn("Failed to delete pet ID: {} by admin: '{}' - pet not found", id, username);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting pet ID: {} by admin: '{}'", id, username, e);
            throw e;
        }
    }
    
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Updates a pet status", description = "Update pet status by ID")
    public ResponseEntity<Pet> updatePetStatus(
            @Parameter(description = "ID of pet to update") @PathVariable Long id,
            @Parameter(description = "New status of pet") @RequestParam PetStatus status) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        logger.info("Admin user '{}' attempting to update status of pet ID: {} to: {}", username, id, status);
        
        try {
            Pet updatedPet = petService.updatePetStatus(id, status);
            if (updatedPet != null) {
                logger.info("Successfully updated pet ID: {} - '{}' status to: {} by admin: '{}'", 
                           id, updatedPet.getName(), status, username);
                return ResponseEntity.ok(updatedPet);
            } else {
                logger.warn("Failed to update status for pet ID: {} by admin: '{}' - pet not found", id, username);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating status of pet ID: {} to: {} by admin: '{}'", 
                        id, status, username, e);
            throw e;
        }
    }
    
    @PostMapping("/{id}/purchase")
    @PreAuthorize("hasAnyRole('USER')")
    @Operation(summary = "Purchase a pet", description = "Purchase an available pet (requires authentication)")
    public ResponseEntity<Pet> purchasePet(
            @Parameter(description = "ID of pet to purchase") @PathVariable Long id) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Pet purchasedPet = petService.purchasePet(id, userOptional.get());
        return purchasedPet != null ? ResponseEntity.ok(purchasedPet) : ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/my-pets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user's pets", description = "Get pets owned and created by the current user (requires authentication)")
    public ResponseEntity<List<Pet>> getMyPets() {
        
        logger.info("Received request to get user's pets (owned and created)");
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            
            if (userOptional.isEmpty()) {
                logger.warn("User with email '{}' not found during getMyPets request", userEmail);
                return ResponseEntity.badRequest().build();
            }
            
            User user = userOptional.get();
            List<Pet> userPets = petService.getPetsByUser(user);
            
            List<Pet> ownedPets = petService.getPetsByOwner(user);
            List<Pet> createdPets = petService.getPetsByCreator(user.getId());
            
            logger.info("Found {} total pets for user '{}': {} owned pets + {} created pets (total {} after deduplication)", 
                       userPets.size(), userEmail, ownedPets.size(), createdPets.size(), userPets.size());
            logger.debug("Owned pets: {}, Created pets: {}", 
                        ownedPets.stream().map(Pet::getName).toList(),
                        createdPets.stream().map(Pet::getName).toList());
            
            return ResponseEntity.ok(userPets);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving user's pets", e);
            throw e;
        }
    }
    
    @GetMapping("/auth-test")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Test authentication", description = "Test endpoint to verify authentication is working")
    public ResponseEntity<?> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        
        User user = userOptional.get();
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Authentication successful",
            "user", user.getEmail(),
            "userId", user.getId(),
            "roles", user.getRoles(),
            "authenticated", auth.isAuthenticated(),
            "principal", auth.getPrincipal().toString()
        ));
    }
}