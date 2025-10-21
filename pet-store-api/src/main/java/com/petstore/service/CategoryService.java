package com.petstore.service;

import com.petstore.exception.CategoryInUseException;
import com.petstore.exception.CategoryAlreadyExistsException;
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


    public Category saveCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new CategoryAlreadyExistsException(
                String.format("Category with name '%s' already exists.", category.getName()));
        }
        return categoryRepository.save(category);
    }

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

    public boolean deleteCategory(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();

        // Check if the category can be safely deleted
        if (!canDeleteCategory(id)) {
            int usageCount = getCategoryUsageCount(id);
            throw new CategoryInUseException(
                String.format("Cannot delete category '%s' (ID: %d) because it is currently being used by %d pet(s)",
                    category.getName(), id, usageCount));
        }

        categoryRepository.deleteById(id);
        return true;
    }


    private int getCategoryUsageCount(Long categoryId) {
        List<Pet> petsUsingCategory = petRepository.findByCategoryId(categoryId);
        return petsUsingCategory.size();
    }


    private boolean canDeleteCategory(Long categoryId) {
        return getCategoryUsageCount(categoryId) == 0;
    }
}