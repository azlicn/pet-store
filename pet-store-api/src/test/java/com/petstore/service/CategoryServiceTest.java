package com.petstore.service;

import com.petstore.model.Category;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        List<Category> expectedCategories = Arrays.asList(testCategory, anotherCategory);
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        List<Category> actualCategories = categoryService.getAllCategories();

        assertThat(actualCategories).hasSize(2);
        assertThat(actualCategories).contains(testCategory, anotherCategory);
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Optional<Category> actualCategory = categoryService.getCategoryById(1L);

        assertThat(actualCategory).isPresent();
        assertThat(actualCategory.get().getName()).isEqualTo("Dogs");
        assertThat(actualCategory.get().getId()).isEqualTo(1L);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldReturnEmpty() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Category> actualCategory = categoryService.getCategoryById(999L);

        assertThat(actualCategory).isEmpty();
        verify(categoryRepository).findById(999L);
    }

    @Test
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

    @Test
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

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldReturnNull() {
        Category categoryDetails = new Category();
        categoryDetails.setName("Updated Category");

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        Category result = categoryService.updateCategory(999L, categoryDetails);

        assertThat(result).isNull();
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldReturnTrue() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(petRepository.findByCategoryId(1L)).thenReturn(Arrays.asList()); // Empty list - no pets using category

        boolean result = categoryService.deleteCategory(1L);

        assertThat(result).isTrue();
        verify(categoryRepository).findById(1L);
        verify(petRepository).findByCategoryId(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotExist_ShouldReturnFalse() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = categoryService.deleteCategory(999L);

        assertThat(result).isFalse();
        verify(categoryRepository).findById(999L);
        verify(petRepository, never()).findByCategoryId(any());
        verify(categoryRepository, never()).deleteById(any());
    }

}