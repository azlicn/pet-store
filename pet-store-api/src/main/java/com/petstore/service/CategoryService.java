package com.petstore.service;

import com.petstore.exception.CategoryAlreadyExistsException;
import com.petstore.exception.CategoryInUseException;
import com.petstore.exception.CategoryNotFoundException;
import com.petstore.exception.InvalidCategoryException;
import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing pet categories
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final PetRepository petRepository;

    public CategoryService(CategoryRepository categoryRepository, PetRepository petRepository) {
        this.categoryRepository = categoryRepository;
        this.petRepository = petRepository;
    }

    /**
     * Retrieves all pet categories
     *
     * @return list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Retrieves a category by its ID
     *
     * @param id the category ID
     * @return the category if found
     */
    public Category getCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    /**
     * Creates a new category
     *
     * @param category the category to create
     * @return the created category
     * @throws InvalidCategoryException       if the category is invalid
     * @throws CategoryAlreadyExistsException if the category name is already in use
     */
    public Category saveCategory(Category category) {

        if (category == null) {
            throw new InvalidCategoryException("Category cannot be null");
        }
        if (categoryRepository.existsByName(category.getName())) {
            throw new CategoryAlreadyExistsException(category.getName());
        }
        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category
     *
     * @param id          the category ID to update
     * @param newCategory the new category details
     * @return the updated category, or null if not found
     * @throws CategoryAlreadyExistsException if the new name is already in use
     */
    public Category updateCategory(Long id, Category newCategory) {

        if (newCategory == null) {
            throw new InvalidCategoryException("Category cannot be null");
        }

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        String newName = newCategory.getName().trim();

        // Check for duplicate name excluding the current category
        if (categoryRepository.existsByName(newName) &&
                !existing.getName().equalsIgnoreCase(newName)) {
            throw new CategoryAlreadyExistsException(
                    String.format("Category with name '%s' already exists.", newName));
        }

        existing.setName(newName);

        return categoryRepository.save(existing);
    }

    /**
     * Deletes a category if it's not in use
     *
     * @param id the category ID to delete
     * @return true if deleted, false if not found
     * @throws CategoryInUseException if the category is being used by pets
     */
    public void deleteCategory(Long id) {
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!canDeleteCategory(id)) {
            int usageCount = getCategoryUsageCount(id);
            throw new CategoryInUseException(category.getName(), usageCount);
        }

        categoryRepository.delete(category);
    }

    /**
     * Counts how many pets are using a category
     *
     * @param categoryId the category ID to check
     * @return number of pets using this category
     */
    private int getCategoryUsageCount(Long categoryId) {
        List<Pet> petsUsingCategory = petRepository.findByCategoryId(categoryId);
        return petsUsingCategory.size();
    }

    /**
     * Checks if a category can be safely deleted
     *
     * @param categoryId the category ID to check
     * @return true if the category can be deleted
     */
    private boolean canDeleteCategory(Long categoryId) {
        return getCategoryUsageCount(categoryId) == 0;
    }
}