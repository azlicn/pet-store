package com.petstore.controller;

import com.petstore.model.Category;
import com.petstore.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Controller Tests")
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Category testCategory;
    private Category secondCategory;
    private List<Category> testCategories;

    @BeforeEach
    void setUp() {

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Dogs");
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("Cats");
        secondCategory.setCreatedAt(LocalDateTime.now());
        secondCategory.setUpdatedAt(LocalDateTime.now());

        testCategories = Arrays.asList(testCategory, secondCategory);
    }

    @Test
    @DisplayName("GET /api/categories - Should return all categories")
    void shouldReturnAllCategories() {

        when(categoryService.getAllCategories()).thenReturn(testCategories);

        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Dogs");
        assertThat(response.getBody().get(1).getName()).isEqualTo("Cats");
    }

    @Test
    @DisplayName("GET /api/categories - Should return empty list when no categories exist")
    void shouldReturnEmptyListWhenNoCategoriesExist() {

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());

        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("GET /api/categories/{id} - Should return category by ID when found")
    void shouldReturnCategoryByIdWhenFound() {

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));

        ResponseEntity<Category> response = categoryController.getCategoryById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Dogs");
    }

    @Test
    @DisplayName("GET /api/categories/{id} - Should return 404 when category not found")
    void shouldReturn404WhenCategoryNotFound() {

        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Category> response = categoryController.getCategoryById(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("POST /api/categories - Should create new category successfully")
    void shouldCreateNewCategorySuccessfully() {

        Category newCategory = new Category();
        newCategory.setName("Birds");

        Category savedCategory = new Category();
        savedCategory.setId(3L);
        savedCategory.setName("Birds");
        savedCategory.setCreatedAt(LocalDateTime.now());
        savedCategory.setUpdatedAt(LocalDateTime.now());

        when(categoryService.saveCategory(any(Category.class))).thenReturn(savedCategory);

        ResponseEntity<Category> response = categoryController.createCategory(newCategory);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(3L);
        assertThat(response.getBody().getName()).isEqualTo("Birds");
        assertThat(response.getBody().getCreatedAt()).isNotNull();
        assertThat(response.getBody().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("POST /api/categories - Should create category with valid name")
    void shouldCreateCategoryWithValidName() {

        Category newCategory = new Category();
        newCategory.setName("Fish & Aquatic");

        Category savedCategory = new Category();
        savedCategory.setId(4L);
        savedCategory.setName("Fish & Aquatic");
        savedCategory.setCreatedAt(LocalDateTime.now());
        savedCategory.setUpdatedAt(LocalDateTime.now());

        when(categoryService.saveCategory(any(Category.class))).thenReturn(savedCategory);

        ResponseEntity<Category> response = categoryController.createCategory(newCategory);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Fish & Aquatic");
    }

    @Test
    @DisplayName("PUT /api/categories/{id} - Should update category successfully")
    void shouldUpdateCategorySuccessfully() {

        Category updateRequest = new Category();
        updateRequest.setName("Updated Dogs");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Dogs");
        updatedCategory.setCreatedAt(testCategory.getCreatedAt());
        updatedCategory.setUpdatedAt(LocalDateTime.now());

        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);

        ResponseEntity<Category> response = categoryController.updateCategory(1L, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("Updated Dogs");
        assertThat(response.getBody().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("PUT /api/categories/{id} - Should return 404 when category not found for update")
    void shouldReturn404WhenCategoryNotFoundForUpdate() {

        Category updateRequest = new Category();
        updateRequest.setName("Non-existent Category");

        when(categoryService.updateCategory(eq(999L), any(Category.class))).thenReturn(null);

        ResponseEntity<Category> response = categoryController.updateCategory(999L, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("PUT /api/categories/{id} - Should handle category name changes")
    void shouldHandleCategoryNameChanges() {

        Category updateRequest = new Category();
        updateRequest.setName("Reptiles & Amphibians");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Reptiles & Amphibians");
        updatedCategory.setCreatedAt(testCategory.getCreatedAt());
        updatedCategory.setUpdatedAt(LocalDateTime.now());

        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);

        ResponseEntity<Category> response = categoryController.updateCategory(1L, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Reptiles & Amphibians");
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} - Should delete category successfully")
    void shouldDeleteCategorySuccessfully() {

        when(categoryService.deleteCategory(1L)).thenReturn(true);

        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} - Should return 404 when category not found for deletion")
    void shouldReturn404WhenCategoryNotFoundForDeletion() {

        when(categoryService.deleteCategory(999L)).thenReturn(false);

        ResponseEntity<Void> response = categoryController.deleteCategory(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("DELETE /api/categories/{id} - Should handle deletion of existing category")
    void shouldHandleDeletionOfExistingCategory() {

        when(categoryService.deleteCategory(2L)).thenReturn(true);

        ResponseEntity<Void> response = categoryController.deleteCategory(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Category operations - Should handle edge case scenarios")
    void shouldHandleEdgeCaseScenarios() {

        when(categoryService.getCategoryById(0L)).thenReturn(Optional.empty());
        ResponseEntity<Category> response = categoryController.getCategoryById(0L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        when(categoryService.deleteCategory(0L)).thenReturn(false);
        ResponseEntity<Void> deleteResponse = categoryController.deleteCategory(0L);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Category operations - Should handle negative IDs")
    void shouldHandleNegativeIds() {

        when(categoryService.getCategoryById(-1L)).thenReturn(Optional.empty());
        ResponseEntity<Category> response = categoryController.getCategoryById(-1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        Category updateRequest = new Category();
        updateRequest.setName("Invalid Category");
        when(categoryService.updateCategory(eq(-1L), any(Category.class))).thenReturn(null);
        ResponseEntity<Category> updateResponse = categoryController.updateCategory(-1L, updateRequest);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        when(categoryService.deleteCategory(-1L)).thenReturn(false);
        ResponseEntity<Void> deleteResponse = categoryController.deleteCategory(-1L);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Category creation - Should handle category with special characters")
    void shouldHandleCategoryWithSpecialCharacters() {

        Category newCategory = new Category();
        newCategory.setName("Exotic Pets & Accessories");

        Category savedCategory = new Category();
        savedCategory.setId(5L);
        savedCategory.setName("Exotic Pets & Accessories");
        savedCategory.setCreatedAt(LocalDateTime.now());
        savedCategory.setUpdatedAt(LocalDateTime.now());

        when(categoryService.saveCategory(any(Category.class))).thenReturn(savedCategory);

        ResponseEntity<Category> response = categoryController.createCategory(newCategory);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Exotic Pets & Accessories");
    }
}