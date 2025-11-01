package com.petstore.controller;

import com.petstore.service.PetService;
import com.petstore.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.petstore.security.JwtTokenProvider;
import com.petstore.service.UserDetailsServiceImpl;
import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.model.User;
import com.petstore.model.Role;
import com.petstore.enums.PetStatus;

import com.petstore.exception.GlobalExceptionHandler;
import com.petstore.exception.PetNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.config.TestSecurityConfig;

/**
 * WebMvcTest for PetController.
 * <p>
 * This test class covers all controller endpoints for pet management, including
 * CRUD operations,
 * status updates, purchase, user-specific queries, and authentication checks.
 * It uses MockMvc for HTTP request simulation
 * and mocks the service and repository layers to isolate controller logic.
 * Security filters are enabled for realistic access control testing.
 */
@WebMvcTest(PetController.class)
@Import({ GlobalExceptionHandler.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pet Controller WebMvcTest")
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test: GET /api/pets
     * Verifies that all pets are returned successfully, with and without filters.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/pets - should return all pets")
    void shouldReturnAllPets() throws Exception {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setPrice(BigDecimal.valueOf(100.0));
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setDescription("Friendly dog");
        pet.setPhotoUrls(List.of("/images/dog1.jpg"));

        List<Pet> pets = List.of(pet);
        Page<Pet> petPage = new PageImpl<>(pets);
        when(petService.findPetsByFiltersPaginated(
                any(),
                any(),
                any(),
                nullable(Long.class),
                anyInt(),
                anyInt())).thenReturn(petPage);

        mockMvc.perform(get("/api/pets?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pets[0].id").value(1))
                .andExpect(jsonPath("$.pets[0].name").value("Buddy"))
                .andExpect(jsonPath("$.pets[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.pets[0].price").value(100.0))
                .andExpect(jsonPath("$.pets[0].description").value("Friendly dog"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(petService, times(1)).findPetsByFiltersPaginated(
                any(),
                any(),
                any(),
                nullable(Long.class),
                anyInt(),
                anyInt());
    }

    /**
     * Test: GET /api/pets/my-pets
     * Verifies that all users's pets are returned successfully, with and without filters.
     */
    @Test
    @WithMockUser(roles = "USER", username = "user@test.com")
    @DisplayName("GET /api/pets/my-pets - should return all user's pets")
    void shouldReturnMyPets() throws Exception {

        User user = new User();
        user.setEmail("user@test.com");
        user.setId(1L);
        user.setRoles(Set.of(Role.USER));

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setPrice(BigDecimal.valueOf(100.0));
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setOwner(user);
        pet.setDescription("Friendly dog");
        pet.setPhotoUrls(List.of("/images/dog1.jpg"));

        List<Pet> pets = List.of(pet);
        Page<Pet> petPage = new PageImpl<>(pets);
        when(petService.findPetsByFiltersPaginated(
                any(),
                any(),
                any(),
                anyLong(),
                anyInt(),
                anyInt())).thenReturn(petPage);
        when(userService.getUserByEmail("user@test.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/pets/my-pets?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pets[0].id").value(1))
                .andExpect(jsonPath("$.pets[0].name").value("Buddy"))
                .andExpect(jsonPath("$.pets[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.pets[0].price").value(100.0))
                .andExpect(jsonPath("$.pets[0].description").value("Friendly dog"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(petService, times(1)).findPetsByFiltersPaginated(
                any(),
                any(),
                any(),
                anyLong(),
                anyInt(),
                anyInt());
    }

    /**
     * Test: GET /api/pets/latest
     * Verifies that the latest pets are returned successfully.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/pets/latest - should return latest pets")
    void shouldReturnLatestPets() throws Exception {

        Pet pet = new Pet();
        pet.setId(10L);
        pet.setName("Milo");
        pet.setPrice(BigDecimal.valueOf(200.0));
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setDescription("Playful kitten");
        pet.setPhotoUrls(List.of("/images/cat1.jpg"));

        when(petService.getLatestAvailablePets(6)).thenReturn(List.of(pet));

        mockMvc.perform(get("/api/pets/latest?limit=6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Milo"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].price").value(200.0))
                .andExpect(jsonPath("$[0].description").value("Playful kitten"));

        verify(petService, times(1)).getLatestAvailablePets(6);
    }

    /**
     * Test: GET /api/pets/{id}
     * Verifies that a pet is returned by ID.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/pets/{id} - should return pet by ID")
    void shouldReturnPetById() throws Exception {

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setPrice(BigDecimal.valueOf(150.0));
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setDescription("Friendly Labrador");
        pet.setPhotoUrls(List.of("/images/dog1.jpg"));

        Category category = new Category();
        category.setId(1L);
        category.setName("Dog");
        pet.setCategory(category);
        pet.setCreatedBy(1L);

        when(petService.getPetById(1L)).thenReturn(pet);

        mockMvc.perform(get("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.price").value(150.0))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.description").value("Friendly Labrador"));

        verify(petService, times(1)).getPetById(1L);
    }

    /**
     * Test: PUT /api/pets/{id}
     * Verifies that updating a pet that does not exist returns Not Found.
     */
    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("PUT /api/pets/{id} - should return 404 when pet not found")
    void shouldReturnNotFoundWhenPetDoesNotExist() throws Exception {

        when(petService.getPetById(1L)).thenThrow(new PetNotFoundException(1L));
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(new User()));

        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setPrice(BigDecimal.valueOf(100.0));
        pet.setDescription("Friendly dog");
        pet.setPhotoUrls(List.of("/images/dog1.jpg"));

        Category category = new Category();
        category.setId(1L);
        category.setName("Dog");

        pet.setCategory(category);

        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    /**
     * Test: PUT /api/pets/{id}
     * Verifies that updating a pet with invalid data returns Bad Request.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/pets/{id} - should return 400 for invalid data")
    void shouldReturnBadRequestWhenInvalidData() throws Exception {

        Pet invalidPet = new Pet();

        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPet)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST /api/pets
     * Verifies that a new pet is added successfully by a user or admin.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/pets - should add pet")
    void shouldAddPet() throws Exception {

        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setCategory(new com.petstore.model.Category("Dog"));
        pet.setPrice(java.math.BigDecimal.valueOf(100.0));
        pet.setDescription("Friendly dog");
        pet.setPhotoUrls(List.of("/images/dog1.jpg"));

        when(petService.savePet(any())).thenReturn(pet);
        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.category.name").value("Dog"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    /**
     * Test: PUT /api/pets/{id}
     * Verifies that an existing pet is updated successfully by owner or admin.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/pets/{id} - should update pet")
    void shouldUpdatePet() throws Exception {

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setPrice(BigDecimal.valueOf(100.0));
        pet.setCategory(new Category("Dog"));
        pet.setDescription("Friendly dog");

        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setId(10L);
        adminUser.setRoles(Set.of(Role.ADMIN));

        when(petService.getPetById(1L)).thenReturn(pet);
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(adminUser));
        when(petService.updatePet(eq(1L), any(Pet.class))).thenReturn(pet);

        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        verify(petService, times(1)).updatePet(eq(1L), any(Pet.class));
    }

    /**
     * Test: DELETE /api/pets/{id}
     * Verifies that a pet is deleted successfully by an admin.
     */
    @Test
    @WithMockUser(roles = "ADMIN")

    void shouldDeletePet() throws Exception {

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");

        when(petService.getPetById(1L)).thenReturn(pet);
        doNothing().when(petService).deletePet(1L);

        mockMvc.perform(delete("/api/pets/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());

        verify(petService, times(1)).deletePet(1L);
    }

    /**
     * Test: GET /api/pets/auth-test
     * Verifies that authentication test endpoint returns user details.
     */
    @Test
    @WithMockUser(roles = "USER", username = "user@test.com")
    @DisplayName("GET /api/pets/auth-test - should return auth test")
    void shouldReturnAuthTest() throws Exception {

        User user = new User();
        user.setEmail("user@test.com");
        user.setId(1L);
        user.setRoles(Set.of(Role.USER));

        when(userService.getUserByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/pets/auth-test"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(
                        content().string(containsString("user@test.com")));
    }

    /**
     * Test: GET /api/pets/my-pets without authentication
     * Verifies that accessing /api/pets/my-pets without login returns 401 Unauthorized.
     */
    @Test
    @DisplayName("GET /api/pets/my-pets - should return 401 when unauthenticated")
    void shouldReturnUnauthorizedForMyPets() throws Exception {
        mockMvc.perform(get("/api/pets/my-pets?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: DELETE /api/pets/{id} without ADMIN role
     * Verifies that deleting a pet without admin role returns 403 Forbidden.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/pets/{id} - should return 403 for non-admin")
    void shouldReturnForbiddenWhenDeletingPetAsUser() throws Exception {
        mockMvc.perform(delete("/api/pets/1"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: GET /api/pets/{id} with invalid ID format
     * Verifies that requesting a pet with invalid ID returns 400 Bad Request.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/pets/{id} - should return 400 for invalid ID format")
    void shouldReturnBadRequestForInvalidPetId() throws Exception {
        mockMvc.perform(get("/api/pets/invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}