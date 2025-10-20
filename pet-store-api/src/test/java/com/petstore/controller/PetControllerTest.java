package com.petstore.controller;

import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.User;
import com.petstore.model.Role;
import com.petstore.service.PetService;
import com.petstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pet Controller Tests")
class PetControllerTest {

    @Mock
    private PetService petService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PetController petController;

    private Pet testPet;
    private Category testCategory;
    private User testUser;
    private List<Pet> testPets;

    @BeforeEach
    void setUp() {

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Dogs");
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRoles(Set.of(Role.USER));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("Buddy");
        testPet.setCategory(testCategory);
        testPet.setPrice(new BigDecimal("299.99"));
        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setCreatedBy(testUser.getId());
        testPet.setCreatedAt(LocalDateTime.now());
        testPet.setUpdatedAt(LocalDateTime.now());

        Pet secondPet = new Pet();
        secondPet.setId(2L);
        secondPet.setName("Max");
        secondPet.setCategory(testCategory);
        secondPet.setPrice(new BigDecimal("399.99"));
        secondPet.setStatus(PetStatus.AVAILABLE);
        secondPet.setCreatedBy(testUser.getId());
        secondPet.setCreatedAt(LocalDateTime.now());
        secondPet.setUpdatedAt(LocalDateTime.now());

        testPets = Arrays.asList(testPet, secondPet);
    }

    @Test
    @DisplayName("GET /api/pets - Should return all pets")
    void shouldReturnAllPets() {

        when(petService.getAllPets()).thenReturn(testPets);

        ResponseEntity<List<Pet>> response = petController.getAllPets(null, null, null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Buddy");
        assertThat(response.getBody().get(1).getName()).isEqualTo("Max");
    }

    @Test
    @DisplayName("GET /api/pets with filters - Should return filtered pets")
    void shouldReturnFilteredPets() {

        when(petService.findPetsByFilters("Buddy", 1L, PetStatus.AVAILABLE, 10))
                .thenReturn(Arrays.asList(testPet));

        ResponseEntity<List<Pet>> response = petController.getAllPets("Buddy", 1L, PetStatus.AVAILABLE, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Buddy");
    }

    @Test
    @DisplayName("GET /api/pets/latest - Should return latest pets with default limit")
    void shouldReturnLatestPetsWithDefaultLimit() {

        when(petService.getLatestAvailablePets(6)).thenReturn(testPets);

        ResponseEntity<List<Pet>> response = petController.getLatestPets(6);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("GET /api/pets/latest - Should return latest pets with custom limit")
    void shouldReturnLatestPetsWithCustomLimit() {

        when(petService.getLatestAvailablePets(3)).thenReturn(testPets);

        ResponseEntity<List<Pet>> response = petController.getLatestPets(3);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("GET /api/pets/{id} - Should return pet by ID when found")
    void shouldReturnPetByIdWhenFound() {

        when(petService.getPetById(1L)).thenReturn(Optional.of(testPet));

        ResponseEntity<Pet> response = petController.getPetById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Buddy");
        assertThat(response.getBody().getPrice()).isEqualTo(new BigDecimal("299.99"));
        assertThat(response.getBody().getStatus()).isEqualTo(PetStatus.AVAILABLE);
    }

    @Test
    @DisplayName("GET /api/pets/{id} - Should return 404 when pet not found")
    void shouldReturn404WhenPetNotFound() {

        when(petService.getPetById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Pet> response = petController.getPetById(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("GET /api/pets/findByStatus - Should return pets by status")
    void shouldReturnPetsByStatus() {

        when(petService.getPetsByStatus(PetStatus.AVAILABLE)).thenReturn(testPets);

        ResponseEntity<List<Pet>> response = petController.findPetsByStatus(PetStatus.AVAILABLE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("POST /api/pets - Should create new pet successfully")
    void shouldCreateNewPetSuccessfully() {

        Pet newPet = new Pet();
        newPet.setName("Charlie");
        newPet.setCategory(testCategory);
        newPet.setPrice(new BigDecimal("199.99"));
        newPet.setStatus(PetStatus.AVAILABLE);

        Pet savedPet = new Pet();
        savedPet.setId(3L);
        savedPet.setName("Charlie");
        savedPet.setCategory(testCategory);
        savedPet.setPrice(new BigDecimal("199.99"));
        savedPet.setStatus(PetStatus.AVAILABLE);

        when(petService.savePet(any(Pet.class))).thenReturn(savedPet);

        ResponseEntity<Pet> response = petController.addPet(newPet);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(3L);
        assertThat(response.getBody().getName()).isEqualTo("Charlie");
        assertThat(response.getBody().getPrice()).isEqualTo(new BigDecimal("199.99"));
    }

    @Test
    @DisplayName("DELETE /api/pets/{id} - Should delete pet successfully")
    void shouldDeletePetSuccessfully() {

        when(petService.deletePet(1L)).thenReturn(true);

        ResponseEntity<Void> response = petController.deletePet(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("DELETE /api/pets/{id} - Should return 404 when pet not found for deletion")
    void shouldReturn404WhenPetNotFoundForDeletion() {

        when(petService.deletePet(999L)).thenReturn(false);

        ResponseEntity<Void> response = petController.deletePet(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("POST /api/pets/{id}/status - Should update pet status successfully")
    void shouldUpdatePetStatusSuccessfully() {

        Pet updatedPet = new Pet();
        updatedPet.setId(1L);
        updatedPet.setName("Buddy");
        updatedPet.setStatus(PetStatus.SOLD);

        when(petService.updatePetStatus(1L, PetStatus.SOLD)).thenReturn(updatedPet);

        ResponseEntity<Pet> response = petController.updatePetStatus(1L, PetStatus.SOLD);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getStatus()).isEqualTo(PetStatus.SOLD);
    }

    @Test
    @DisplayName("POST /api/pets/{id}/status - Should return 404 when pet not found for status update")
    void shouldReturn404WhenPetNotFoundForStatusUpdate() {

        when(petService.updatePetStatus(999L, PetStatus.SOLD)).thenReturn(null);

        ResponseEntity<Pet> response = petController.updatePetStatus(999L, PetStatus.SOLD);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("PUT /api/pets/{id} - Should update pet successfully when user is owner")
    void shouldUpdatePetSuccessfullyWhenUserIsOwner() {

        Pet updatedPet = new Pet();
        updatedPet.setId(1L);
        updatedPet.setName("Buddy Updated");
        updatedPet.setCategory(testCategory);
        updatedPet.setPrice(new BigDecimal("349.99"));
        updatedPet.setStatus(PetStatus.AVAILABLE);

        Pet updateRequest = new Pet();
        updateRequest.setName("Buddy Updated");
        updateRequest.setPrice(new BigDecimal("349.99"));

        // Mock SecurityContext
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(petService.getPetById(1L)).thenReturn(Optional.of(testPet));
            when(petService.updatePet(eq(1L), any(Pet.class))).thenReturn(updatedPet);

            ResponseEntity<Pet> response = petController.updatePet(1L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(1L);
            assertThat(response.getBody().getName()).isEqualTo("Buddy Updated");
            assertThat(response.getBody().getPrice()).isEqualTo(new BigDecimal("349.99"));
        }
    }

    @Test
    @DisplayName("PUT /api/pets/{id} - Should return 404 when pet not found for update")
    void shouldReturn404WhenPetNotFoundForUpdate() {

        Pet updateRequest = new Pet();
        updateRequest.setName("Updated Name");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(petService.getPetById(999L)).thenReturn(Optional.empty());

            ResponseEntity<Pet> response = petController.updatePet(999L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNull();
        }
    }

    @Test
    @DisplayName("POST /api/pets/{id}/purchase - Should purchase pet successfully")
    void shouldPurchasePetSuccessfully() {

        Pet purchasedPet = new Pet();
        purchasedPet.setId(1L);
        purchasedPet.setName("Buddy");
        purchasedPet.setStatus(PetStatus.SOLD);
        purchasedPet.setOwner(testUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(petService.purchasePet(1L, testUser)).thenReturn(purchasedPet);

            ResponseEntity<Pet> response = petController.purchasePet(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(1L);
            assertThat(response.getBody().getStatus()).isEqualTo(PetStatus.SOLD);
        }
    }

    @Test
    @DisplayName("POST /api/pets/{id}/purchase - Should return 400 when purchase fails")
    void shouldReturn400WhenPurchaseFails() {

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(petService.purchasePet(1L, testUser)).thenReturn(null);

            ResponseEntity<Pet> response = petController.purchasePet(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNull();
        }
    }

    @Test
    @DisplayName("GET /api/pets/my-pets - Should return user's pets")
    void shouldReturnUsersPets() {

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(petService.getPetsByUser(testUser)).thenReturn(Arrays.asList(testPet));
            when(petService.getPetsByOwner(testUser)).thenReturn(Arrays.asList(testPet));
            when(petService.getPetsByCreator(testUser.getId())).thenReturn(Arrays.asList());

            ResponseEntity<List<Pet>> response = petController.getMyPets();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody().get(0).getId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("GET /api/pets/auth-test - Should return authentication details")
    void shouldReturnAuthenticationDetails() {

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            ResponseEntity<?> response = petController.testAuth();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }

    @Test
    @DisplayName("GET /api/pets/auth-test - Should return 400 when user not found")
    void shouldReturn400WhenUserNotFoundInAuthTest() {

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            ResponseEntity<?> response = petController.testAuth();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isEqualTo("User not found");
        }
    }
}