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
 * Integration tests for Category Management API endpoints.
 * Tests the full flow from HTTP request to database and back.
 */
class CategoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PetRepository petRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Set up security context for JPA auditing
        UserPrincipal adminPrincipal = UserPrincipal.create(testAdmin);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(adminPrincipal, null, adminPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create test category
        testCategory = new Category();
        testCategory.setName("Dogs");
        testCategory = categoryRepository.save(testCategory);
        
        // Clear security context after setup
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAllCategories_Success() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetAllCategories_WithMultipleCategories() throws Exception {
        // Create additional categories
        Category category2 = new Category();
        category2.setName("Cats");
        categoryRepository.save(category2);

        Category category3 = new Category();
        category3.setName("Birds");
        categoryRepository.save(category3);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void testGetCategoryById_Success() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", testCategory.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategory.getId()))
                .andExpect(jsonPath("$.name").value("Dogs"));
    }

    @Test
    void testGetCategoryById_NotFound() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", 99999L)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCategoryById_Forbidden_AsRegularUser() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", testCategory.getId())
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetCategoryById_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", testCategory.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCategory_Success() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("Reptiles");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Reptiles"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateCategory_WithMinimalData() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("Fish");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fish"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateCategory_Forbidden_AsRegularUser() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("Hamsters");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCategory_Unauthorized() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("Rabbits");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCategory_ValidationError_EmptyName() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCategory_Success() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("Canines");

        mockMvc.perform(put("/api/categories/{id}", testCategory.getId())
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategory.getId()))
                .andExpect(jsonPath("$.name").value("Canines"));
    }

    @Test
    void testUpdateCategory_NotFound() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("NonExistent");

        mockMvc.perform(put("/api/categories/{id}", 99999L)
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCategory_Forbidden_AsRegularUser() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("Updated Name");

        mockMvc.perform(put("/api/categories/{id}", testCategory.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCategory_ValidationError_EmptyName() throws Exception {
        Category categoryRequest = new Category();
        categoryRequest.setName("");

        mockMvc.perform(put("/api/categories/{id}", testCategory.getId())
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteCategory_Success() throws Exception {
        // Create a category with no pets
        Category categoryToDelete = new Category();
        categoryToDelete.setName("Temporary Category");
        categoryToDelete = categoryRepository.save(categoryToDelete);

        mockMvc.perform(delete("/api/categories/{id}", categoryToDelete.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNoContent());

        // Verify category is deleted
        mockMvc.perform(get("/api/categories/{id}", categoryToDelete.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCategory_NotFound() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 99999L)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCategory_Forbidden_AsRegularUser() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", testCategory.getId())
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCategory_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", testCategory.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCategory_WithAssociatedPets_ShouldFail() throws Exception {
        // Set up security context for creating pet
        UserPrincipal userPrincipal = UserPrincipal.create(testUser);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create a pet associated with testCategory
        Pet pet = new Pet();
        pet.setName("Test Pet");
        pet.setDescription("Test Description");
        pet.setPrice(new BigDecimal("100.00"));
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setCategory(testCategory);
        pet.setOwner(testUser);
        petRepository.save(pet);

        SecurityContextHolder.clearContext();

        // Try to delete category with associated pets - should fail
        mockMvc.perform(delete("/api/categories/{id}", testCategory.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCategoryLifecycle_CreateUpdateDelete() throws Exception {
        // Create
        Category categoryRequest = new Category();
        categoryRequest.setName("Lifecycle Test");

        String createResponse = mockMvc.perform(post("/api/categories")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Lifecycle Test"))
                .andReturn().getResponse().getContentAsString();

        Category createdCategory = objectMapper.readValue(createResponse, Category.class);
        Long categoryId = createdCategory.getId();

        // Update
        categoryRequest.setName("Lifecycle Test Updated");

        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lifecycle Test Updated"));

        // Delete
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/categories/{id}", categoryId)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }
}
