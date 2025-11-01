package com.petstore.integration;

import com.petstore.enums.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import com.petstore.security.UserPrincipal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Pet Management API endpoints.
 * Tests the full flow from HTTP request to database and back.
 */
class PetIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        // Set up security context for JPA auditing
        UserPrincipal userPrincipal = UserPrincipal.create(testUser);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create test category
        testCategory = new Category();
        testCategory.setName("Dogs");
        testCategory = categoryRepository.save(testCategory);

        // Create test pet owned by testUser
        testPet = new Pet();
        testPet.setName("Buddy");
        testPet.setDescription("Friendly Golden Retriever");
        testPet.setPrice(new BigDecimal("500.00"));
        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setCategory(testCategory);
        testPet.setOwner(testUser); // Set owner to testUser
        testPet = petRepository.save(testPet);
        
        // Clear security context after setup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAllPets_Success() throws Exception {
        mockMvc.perform(get("/api/pets")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets").isArray())
                .andExpect(jsonPath("$.pets", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.pets[0].name").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void testGetPetById_Success() throws Exception {
        mockMvc.perform(get("/api/pets/{id}", testPet.getId())
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testPet.getId()))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.description").value("Friendly Golden Retriever"))
                .andExpect(jsonPath("$.price").value(500.00))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void testGetPetById_NotFound() throws Exception {
        mockMvc.perform(get("/api/pets/{id}", 99999L)
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePet_Success() throws Exception {
        // Create a Pet object with category
        Pet petRequest = new Pet();
        petRequest.setName("Max");
        petRequest.setDescription("Friendly Labrador");
        petRequest.setPrice(new BigDecimal("450.00"));
        petRequest.setCategory(testCategory);

        mockMvc.perform(post("/api/pets")
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.description").value("Friendly Labrador"))
                .andExpect(jsonPath("$.price").value(450.00))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void testCreatePet_Unauthorized() throws Exception {
        Pet petRequest = new Pet();
        petRequest.setName("Max");
        petRequest.setPrice(new BigDecimal("450.00"));
        petRequest.setCategory(testCategory);

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petRequest)))
                .andExpect(status().isForbidden()); // Spring Security returns 403 for missing auth
    }

    @Test
    void testUpdatePet_Success() throws Exception {
        // Create a Pet object with category
        Pet petRequest = new Pet();
        petRequest.setName("Buddy Updated");
        petRequest.setDescription("Updated description");
        petRequest.setPrice(new BigDecimal("600.00"));
        petRequest.setCategory(testCategory);

        mockMvc.perform(put("/api/pets/{id}", testPet.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Buddy Updated"))
                .andExpect(jsonPath("$.price").value(600.00));
    }

    @Test
    void testDeletePet_Success_AsAdmin() throws Exception {
        mockMvc.perform(delete("/api/pets/{id}", testPet.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNoContent());

        // Verify pet is deleted (need auth to access)
        mockMvc.perform(get("/api/pets/{id}", testPet.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePet_Forbidden_AsRegularUser() throws Exception {
        // Create a pet owned by admin
        Pet adminPet = new Pet();
        adminPet.setName("Admin Pet");
        adminPet.setDescription("Cat for admin");
        adminPet.setPrice(new BigDecimal("300.00"));
        adminPet.setStatus(PetStatus.AVAILABLE);
        adminPet.setCategory(testCategory);
        adminPet.setCreatedBy(testAdmin.getId());
        adminPet = petRepository.save(adminPet);

        // Regular user tries to delete admin's pet
        mockMvc.perform(delete("/api/pets/{id}", adminPet.getId())
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFilterPetsByCategory_Success() throws Exception {
        mockMvc.perform(get("/api/pets")
                .param("categoryId", testCategory.getId().toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets").isArray())
                .andExpect(jsonPath("$.pets[0].category.id").value(testCategory.getId()));
    }

    @Test
    void testFilterPetsByStatus_Success() throws Exception {
        mockMvc.perform(get("/api/pets")
                .param("status", "AVAILABLE")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets").isArray())
                .andExpect(jsonPath("$.pets[*].status", everyItem(is("AVAILABLE"))));
    }

    @Test
    void testSearchPetsByName_Success() throws Exception {
        mockMvc.perform(get("/api/pets")
                .param("name", "Buddy")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets").isArray())
                .andExpect(jsonPath("$.pets[0].name", containsStringIgnoringCase("Buddy")));
    }

    @Test
    void testGetMyPets_Success() throws Exception {
        mockMvc.perform(get("/api/pets/my-pets")
                .header("Authorization", createAuthorizationHeader(userToken))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets").isArray());
    }

    @Test
    void testGetLatestPets_Success() throws Exception {
        mockMvc.perform(get("/api/pets/latest")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(5))));
    }
}
