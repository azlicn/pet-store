package com.petstore.controller;

import com.petstore.exception.CategoryAlreadyExistsException;
import com.petstore.exception.CategoryNotFoundException;
import com.petstore.exception.ErrorCodes;
import com.petstore.exception.GlobalExceptionHandler;
import com.petstore.model.Category;
import com.petstore.security.JwtTokenProvider;
import com.petstore.service.CategoryService;
import com.petstore.service.UserDetailsServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.petstore.config.TestSecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * WebMvcTest for CategoryController.
 * <p>
 * This test class covers all controller endpoints for category management, including CRUD operations,
 * edge cases, negative scenarios, and security/authorization checks. It uses MockMvc for HTTP request simulation
 * and mocks the service layer to isolate controller logic. Security filters are enabled for realistic access control testing.
 */
@WebMvcTest(CategoryController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Category Controller WebMvcTest")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test: GET /api/categories
     * Verifies that all categories are returned successfully for an authenticated admin user.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/categories - should return all categories")
    void shouldReturnAllCategories() throws Exception {
        Category cat1 = new Category(); cat1.setId(1L); cat1.setName("Dogs");
        Category cat2 = new Category(); cat2.setId(2L); cat2.setName("Cats");
        when(categoryService.getAllCategories()).thenReturn(List.of(cat1, cat2));
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Dogs"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Cats"));
    }

    /**
     * Test: GET /api/categories/{id}
     * Verifies that a category is returned by ID for an authenticated admin user.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/categories/{id} - should return category by id")
    void shouldReturnCategoryById() throws Exception {
        Category cat = new Category(); cat.setId(1L); cat.setName("Dogs");
        when(categoryService.getCategoryById(1L)).thenReturn(cat);
        mockMvc.perform(get("/api/categories/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Dogs"));
    }

    /**
     * Test: GET /api/categories/{id} (not found)
     * Verifies that a 404 is returned if the category does not exist.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/categories/{id} - should return 404 if not found")
    void shouldReturn404IfCategoryNotFound() throws Exception {

        when(categoryService.getCategoryById(99L)).thenThrow(new CategoryNotFoundException(99L));

        mockMvc.perform(get("/api/categories/99"))
            .andExpect(status().isNotFound());
    }

    /**
     * Test: POST /api/categories
     * Verifies that a new category is created successfully for an admin user.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/categories - should create category successfully")
    void shouldCreateCategorySuccessfully() throws Exception {

        Category newCategory = new Category(); newCategory.setName("Birds");
        Category savedCategory = new Category(); savedCategory.setId(1L); savedCategory.setName("Birds");
        when(categoryService.saveCategory(any(Category.class))).thenReturn(savedCategory);
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Birds"));
    }

    /**
     * Test: POST /api/categories (duplicate name)
     * Verifies that a conflict error is returned when creating a category with a duplicate name.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/categories - should handle duplicate category name error")
    void shouldHandleDuplicateCategoryNameError() throws Exception {
        
        Category newCategory = new Category(); newCategory.setName("Dogs");
        when(categoryService.saveCategory(any(Category.class))).thenThrow(new CategoryAlreadyExistsException(newCategory.getName()));
        mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newCategory)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("Category Already Exists"))
            .andExpect(jsonPath("$.message").value("Category with name 'Dogs' already exists."))
            .andExpect(jsonPath("$.code").value(ErrorCodes.CATEGORY_ALREADY_EXISTS));
    }

    /**
     * Test: PUT /api/categories/{id}
     * Verifies that an existing category is updated successfully for an admin user.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/categories/{id} - should update category successfully")
    void shouldUpdateCategorySuccessfully() throws Exception {

        Category updateDetails = new Category(); updateDetails.setName("Reptiles");
        Category updatedCategory = new Category(); updatedCategory.setId(1L); updatedCategory.setName("Reptiles");

        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);
        mockMvc.perform(put("/api/categories/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDetails)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Reptiles"));
    }

    /**
     * Test: PUT /api/categories/{id} (not found)
     * Verifies that a 404 is returned if the category to update does not exist.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/categories/{id} - should return 404 if category not found")
    void shouldReturn404OnUpdateIfNotFound() throws Exception {

        Category updateDetails = new Category(); updateDetails.setName("Reptiles");
        when(categoryService.updateCategory(99L, updateDetails)).thenReturn(null);
        mockMvc.perform(put("/api/categories/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDetails)))
            .andExpect(status().isNotFound());
    }

    /**
     * Test: DELETE /api/categories/{id}
     * Verifies that a category is deleted successfully for an admin user.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/categories/{id} - should delete category successfully")
    void shouldDeleteCategorySuccessfully() throws Exception {

        doNothing().when(categoryService).deleteCategory(1L);
        mockMvc.perform(delete("/api/categories/1"))
            .andExpect(status().isNoContent());
    }

    /**
     * Test: DELETE /api/categories/{id} (not found)
     * Verifies that a 404 is returned if the category to delete does not exist.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/categories/{id} - should return 404 if category not found")
    void shouldReturn404OnDeleteIfNotFound() throws Exception {

         doThrow(new CategoryNotFoundException(99L)).when(categoryService).deleteCategory(99L);
        mockMvc.perform(delete("/api/categories/99"))
            .andExpect(status().isNotFound());
    }

    // Edge case and negative scenario tests
    /**
     * Test: POST /api/categories (empty name)
     * Verifies that a 400 Bad Request is returned when the category name is empty.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/categories - should return 400 for empty name")
    void shouldReturn400ForEmptyCategoryName() throws Exception {

        Category newCategory = new Category(); newCategory.setName("");
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST /api/categories (null body)
     * Verifies that a 400 Bad Request is returned when the request body is empty.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/categories - should return 400 for null body")
    void shouldReturn400ForNullBody() throws Exception {
        
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("") 
        ).andExpect(status().isBadRequest());
    }

    /**
     * Test: GET /api/categories/{id} (unauthorized)
     * Verifies that a 403 Forbidden is returned for a user without admin role.
     */
    @Test
    @DisplayName("GET /api/categories/{id} - should return 403 for unauthorized user")
    void shouldReturn403ForUnauthorizedUserOnGetById() throws Exception {

        mockMvc.perform(get("/api/categories/1"))
            .andExpect(status().isForbidden());
    }

    /**
     * Test: POST /api/categories (unauthorized)
     * Verifies that a 403 Forbidden is returned for a user without admin role when creating a category.
     */
    @Test
    @DisplayName("POST /api/categories - should return 403 for unauthorized user")
    void shouldReturn403ForUnauthorizedUserOnCreate() throws Exception {

        Category newCategory = new Category(); newCategory.setName("Fish");
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isForbidden());
    }
}