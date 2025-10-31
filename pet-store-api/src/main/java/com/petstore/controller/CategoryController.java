package com.petstore.controller;

import com.petstore.model.Category;
import com.petstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing categories in the store.
 * Provides endpoints for CRUD operations on categories.
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves a list of all categories.
     *
     * @return ResponseEntity containing the list of categories
     */
    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves a single category by its ID.
     *
     * @param id the category ID
     * @return ResponseEntity containing the category if found, or not found status
     */
    @Operation(summary = "Get category by ID", description = "Retrieve a single category by its ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {

        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * Adds a new category to the store.
     *
     * @param category the category to create
     * @return ResponseEntity containing the created category
     */
    @Operation(summary = "Create a new category", description = "Add a new category to the store")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    /**
     * Updates an existing category.
     *
     * @param id              the category ID to update
     * @param categoryDetails the updated category data
     * @return ResponseEntity containing the updated category if found, or not found
     *         status
     */
    @Operation(summary = "Update a category", description = "Update an existing category")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
            @Valid @RequestBody Category categoryDetails) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a category from the store. Cannot delete if category is being used by
     * pets.
     *
     * @param id the category ID to delete
     * @return ResponseEntity with no content if deleted, or not found status
     */
    @Operation(summary = "Delete a category", description = "Delete a category from the store. Cannot delete if category is being used by pets.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);
        
        return ResponseEntity.noContent().build();
    }
}