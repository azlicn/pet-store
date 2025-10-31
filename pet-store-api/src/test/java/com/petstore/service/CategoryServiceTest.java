package com.petstore.service;

import com.petstore.exception.CategoryInUseException;
import com.petstore.exception.CategoryNotFoundException;
import com.petstore.exception.InvalidCategoryException;
import com.petstore.model.Category;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Unit tests for {@link CategoryService} covering CRUD operations and edge cases.
 * Uses Mockito for mocking dependencies and AssertJ for assertions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private Category anotherCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Dogs");

        anotherCategory = new Category();
        anotherCategory.setId(2L);
        anotherCategory.setName("Cats");
    }

    /**
     * Test: Should return all categories from repository.
     */
    @Test
    @DisplayName("Get all categories - Should return all categories")
    void getAllCategories_ShouldReturnAllCategories() {
        List<Category> expectedCategories = Arrays.asList(testCategory, anotherCategory);
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        List<Category> actualCategories = categoryService.getAllCategories();

        assertThat(actualCategories).hasSize(2);
        assertThat(actualCategories).contains(testCategory, anotherCategory);
        verify(categoryRepository).findAll();
    }

    /**
     * Test: Should return category when it exists by ID.
     */
    @Test
    @DisplayName("Get category by ID - Should return category when exists")
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Category actualCategory = categoryService.getCategoryById(1L);

        assertThat(actualCategory).isNotNull();
        assertThat(actualCategory.getName()).isEqualTo("Dogs");
        assertThat(actualCategory.getId()).isEqualTo(1L);
        verify(categoryRepository).findById(1L);
    }

    /**
     * Test: Should return empty Optional when category does not exist by ID.
     */
    @Test
    @DisplayName("Get category by ID - Should return empty when does not exist")
    void getCategoryById_WhenCategoryDoesNotExist_ShouldReturnEmpty() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
            .isInstanceOf(com.petstore.exception.CategoryNotFoundException.class)
            .hasMessageContaining("Category not found");
        verify(categoryRepository).findById(999L);
    }

    /**
     * Test: Should save and return category when valid.
     */
    @Test
    @DisplayName("Save category - Should save and return category")
    void saveCategory_ShouldSaveAndReturnCategory() {
        Category newCategory = new Category();
        newCategory.setName("Fish");

        Category savedCategory = new Category();
        savedCategory.setId(3L);
        savedCategory.setName("Fish");

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.saveCategory(newCategory);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Fish");
        verify(categoryRepository).save(newCategory);
    }

    /**
     * Test: Should update and return category when it exists.
     */
    @Test
    @DisplayName("Update category - Should update and return category when exists")
    void updateCategory_WhenCategoryExists_ShouldUpdateAndReturnCategory() {
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Dogs");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Dogs");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(1L, categoryDetails);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Dogs");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    /**
     * Test: Should return null when updating a non-existent category.
     */
    @Test
    @DisplayName("Update category - Should return null when does not exist")
    void updateCategory_WhenCategoryDoesNotExist_ShouldReturnNull() {
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(999L, categoryDetails))
            .isInstanceOf(com.petstore.exception.CategoryNotFoundException.class)
            .hasMessageContaining("Category not found");
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    /**
     * Test: Should return true when deleting a category that exists and is not in use.
     */
    @Test
    @DisplayName("Delete category - Should delete when exists and no pets")
    void deleteCategory_WhenCategoryExistsAndNoPets_ShouldDeleteCategory() {
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(petRepository.findByCategoryId(1L)).thenReturn(Arrays.asList()); // Empty list - no pets using category

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(petRepository).findByCategoryId(1L);
        verify(categoryRepository).delete(testCategory);
    }

    /**
     * Test: Should return false when deleting a non-existent category.
     */
    @Test
    @DisplayName("Delete category - Should return false when does not exist")
    void deleteCategory_WhenCategoryDoesNotExist_ShouldReturnFalse() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
            .isInstanceOf(CategoryNotFoundException.class)
            .hasMessageContaining("Category not found");

        verify(categoryRepository).findById(999L);
        verify(petRepository, never()).findByCategoryId(any());
        verify(categoryRepository, never()).deleteById(any());
    }

    /**
     * Test: Should throw InvalidCategoryException when saving a null category.
     */
    @Test
    @DisplayName("Save category - Should throw InvalidCategoryException when category is null")
    void saveCategory_WhenCategoryIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> categoryService.saveCategory(null))
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessageContaining("Category cannot be null");
        verify(categoryRepository, never()).save(any());
    }

    /**
     * Test: Should handle saving a category with a null name.
     */
    @Test
    @DisplayName("Save category - Should handle null name")
    void saveCategory_WhenNameIsNull_ShouldHandleGracefully() {
        Category newCategory = new Category();
        newCategory.setName(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
        Category result = categoryService.saveCategory(newCategory);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
        verify(categoryRepository).save(newCategory);
    }

    /**
     * Test: Should throw InvalidCategoryException when updating with null details.
     */
    @Test
    @DisplayName("Update category - Should throw InvalidCategoryException when details are null")
    void updateCategory_WhenDetailsAreNull_ShouldThrowException() {
    assertThatThrownBy(() -> categoryService.updateCategory(1L, null))
        .isInstanceOf(InvalidCategoryException.class)
        .hasMessageContaining("Category cannot be null");
    verify(categoryRepository, never()).save(any());
    }

    /**
     * Test: Should update category with an empty name.
     */
    @Test
    @DisplayName("Update category - Should handle empty name")
    void updateCategory_WhenNameIsEmpty_ShouldUpdateWithEmptyName() {
        Category categoryDetails = new Category();
        categoryDetails.setName("");
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        Category result = categoryService.updateCategory(1L, categoryDetails);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEmpty();
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    /**
     * Test: Should throw CategoryInUseException when deleting a category in use by pets.
     */
    @Test
    @DisplayName("Delete category - Should throw CategoryInUseException when pets exist for category")
    void deleteCategory_WhenPetsExist_ShouldThrowCategoryInUseException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(petRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(mock(com.petstore.model.Pet.class)));
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(CategoryInUseException.class);
        verify(categoryRepository).findById(1L);
       verify(petRepository, times(2)).findByCategoryId(1L);
        verify(categoryRepository, never()).deleteById(any());
    }
}