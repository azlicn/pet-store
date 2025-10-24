package com.petstore.service;

import com.petstore.exception.CategoryAlreadyExistsException;
import com.petstore.exception.CategoryInUseException;
import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing pet categories
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PetRepository petRepository;

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
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Creates a new category
     *
     * @param category the category to create
     * @return the created category
     * @throws CategoryAlreadyExistsException if the category name is already in use
     */
    public Category saveCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new CategoryAlreadyExistsException(
                String.format("Category with name '%s' already exists.", category.getName()));
        }
        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category
     *
     * @param id the category ID to update
     * @param categoryDetails the new category details
     * @return the updated category, or null if not found
     * @throws CategoryAlreadyExistsException if the new name is already in use
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Optional<Category> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isPresent()) {
            // Check for duplicate name (excluding current category)
            if (categoryRepository.existsByName(categoryDetails.getName()) &&
                !existingCategory.get().getName().equals(categoryDetails.getName())) {
                throw new CategoryAlreadyExistsException(
                    String.format("Category with name '%s' already exists.", categoryDetails.getName()));
            }
            Category category = existingCategory.get();
            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        }

        return null;
    }

    /**
     * Deletes a category if it's not in use
     *
     * @param id the category ID to delete
     * @return true if deleted, false if not found
     * @throws CategoryInUseException if the category is being used by pets
     */
    public boolean deleteCategory(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();

        // Check if the category can be safely deleted
        if (!canDeleteCategory(id)) {
            int usageCount = getCategoryUsageCount(id);
            throw new CategoryInUseException(category.getName(), usageCount);
        }

        categoryRepository.deleteById(id);
        return true;
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