package com.petstore.controller;

import com.petstore.enums.PetStatus;
import com.petstore.model.Pet;
import com.petstore.model.User;
import com.petstore.model.Role;
import com.petstore.service.PetService;
import com.petstore.service.UserService;
import com.petstore.dto.PetPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing pets in the store.
 * Provides endpoints for CRUD operations, status updates, purchase, and
 * user-specific pet queries.
 */
@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pet Controller", description = "Pet API")
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    private final PetService petService;

    private final UserService userService;

    public PetController(PetService petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
    }

    /**
     * Retrieves all pets, optionally filtered by name, category, status,
     * or limit.
     *
     * @param name       optional pet name filter
     * @param categoryId optional category ID filter
     * @param status     optional pet status filter
     * @param limit      optional limit on number of results
     * @return ResponseEntity containing the list of pets
     */
    @GetMapping
    @Operation(summary = "Get pets", description = "Retrieve pets for purchase (public access)")
    public ResponseEntity<?> getAllPets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) PetStatus status,
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Pet> petPage = petService.findPetsByFiltersPaginated(name, categoryId, status, null, page, size);
        PetPageResponse response = new PetPageResponse(
                petPage.getContent(),
                petPage.getNumber(),
                petPage.getSize(),
                petPage.getTotalElements(),
                petPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the latest available pets, limited by the specified number.
     *
     * @param limit the maximum number of pets to return
     * @return ResponseEntity containing the list of latest pets
     */
    @GetMapping("/latest")
    @Operation(summary = "Get latest available pets", description = "Retrieve the latest available pets (useful for home page)")
    public ResponseEntity<?> getLatestPets(
            @RequestParam(required = false) Integer limit) {

        List<Pet> pets = petService.getLatestAvailablePets(limit);
        return ResponseEntity.ok(pets);
    }

    /**
     * Retrieves a pet by its ID.
     *
     * @param id the ID of the pet to retrieve
     * @return ResponseEntity containing the pet if found, or not found status
     */
    @GetMapping("/{id}")
    @Operation(summary = "Find pet by ID", description = "Returns a single pet")
    public ResponseEntity<Pet> getPetById(
            @Parameter(description = "ID of pet to return") @PathVariable Long id) {

        return ResponseEntity.ok(petService.getPetById(id));

    }

    /**
     * Adds a new pet to the store.
     *
     * @param pet the pet to add
     * @return ResponseEntity containing the added pet
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Add a new pet", description = "Add a new pet to the store")
    public ResponseEntity<Pet> addPet(@Valid @RequestBody Pet pet) {

        Pet savedPet = petService.savePet(pet);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPet.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedPet);
    }

    /**
     * Updates an existing pet by its ID.
     * Only the owner or an admin can update a pet.
     *
     * @param id         the ID of the pet to update
     * @param petDetails the updated pet details
     * @return ResponseEntity containing the updated pet if successful, or error
     *         status
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update an existing pet", description = "Update an existing pet by Id")
    public ResponseEntity<Pet> updatePet(
            @Parameter(description = "ID of pet to update") @PathVariable Long id,
            @Valid @RequestBody Pet petDetails) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Pet existingPet = petService.getPetById(id);

        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
        boolean isOwner = existingPet.getCreatedBy() != null &&
                existingPet.getCreatedBy().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to update this pet");
        }

        Pet updatedPet = petService.updatePet(id, petDetails);
        return ResponseEntity.ok(updatedPet);

    }

    /**
     * Deletes a pet by its ID. Only admins can delete pets.
     *
     * @param id the ID of the pet to delete
     * @return ResponseEntity with status OK if deleted, or not found status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletes a pet", description = "Delete a pet by ID")
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "Pet id to delete") @PathVariable Long id) {

        petService.deletePet(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves pets owned and created by the current authenticated user.
     *
     * @return ResponseEntity containing the list of user's pets
     */
    @GetMapping("/my-pets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user's pets", description = "Get pets owned and created by the current user (requires authentication)")
    public ResponseEntity<?> getMyPets(

            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) PetStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Page<Pet> petPage = petService.findPetsByFiltersPaginated(name, categoryId, status, user.getId(), page, size);
        PetPageResponse response = new PetPageResponse(
                petPage.getContent(),
                petPage.getNumber(),
                petPage.getSize(),
                petPage.getTotalElements(),
                petPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint to verify authentication is working for the current user.
     *
     * @return ResponseEntity containing authentication details for the user
     */
    @GetMapping("/auth-test")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Test authentication", description = "Test endpoint to verify authentication is working")
    public ResponseEntity<?> testAuth() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        User user = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok(Map.of(
                "message", "Authentication successful",
                "user", user.getEmail(),
                "userId", user.getId(),
                "roles", user.getRoles(),
                "authenticated", auth.isAuthenticated(),
                "principal", auth.getPrincipal().toString()));
    }
}