package com.petstore.service;

import com.petstore.exception.CategoryInUseException;
import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PetRepository petRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Optional<Category> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();
            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        }

        return null;
    }

    public boolean deleteCategory(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();

        // Check if any pets are using this category
        List<Pet> petsUsingCategory = petRepository.findByCategoryId(id);

        if (!petsUsingCategory.isEmpty()) {
            throw new CategoryInUseException(
                    id,
                    category.getName(),
                    petsUsingCategory.size());
        }

        categoryRepository.deleteById(id);
        return true;
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    /**
     * Check if a category is being used by any pets
     * 
     * @param categoryId the category ID to check
     * @return the number of pets using this category
     */
    public int getCategoryUsageCount(Long categoryId) {
        List<Pet> petsUsingCategory = petRepository.findByCategoryId(categoryId);
        return petsUsingCategory.size();
    }

    /**
     * Check if a category can be safely deleted (not being used by any pets)
     * 
     * @param categoryId the category ID to check
     * @return true if the category can be deleted, false otherwise
     */
    public boolean canDeleteCategory(Long categoryId) {
        return getCategoryUsageCount(categoryId) == 0;
    }
}