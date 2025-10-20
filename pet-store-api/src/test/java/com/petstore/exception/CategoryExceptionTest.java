package com.petstore.exception;

import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import com.petstore.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for CategoryInUseException and related exception handling
 */
@ExtendWith(MockitoExtension.class)
class CategoryExceptionTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private Pet testPet1;
    private Pet testPet2;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Dogs");

        testPet1 = new Pet();
        testPet1.setId(1L);
        testPet1.setName("Buddy");
        testPet1.setCategory(testCategory);
        testPet1.setPrice(new BigDecimal("299.99"));
        testPet1.setStatus(PetStatus.AVAILABLE);

        testPet2 = new Pet();
        testPet2.setId(2L);
        testPet2.setName("Max");
        testPet2.setCategory(testCategory);
        testPet2.setPrice(new BigDecimal("399.99"));
        testPet2.setStatus(PetStatus.AVAILABLE);
    }

    @Test
    void testDeleteCategory_Success_WhenNosPetsUsingCategory() {
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(petRepository.findByCategoryId(1L)).thenReturn(Collections.emptyList());

        
        boolean result = categoryService.deleteCategory(1L);

        
        assertTrue(result);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategory_ThrowsException_WhenPetsUsingCategory() {
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(petRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(testPet1, testPet2));

        CategoryInUseException exception = assertThrows(
            CategoryInUseException.class,
            () -> categoryService.deleteCategory(1L)
        );

        assertEquals(1L, exception.getCategoryId());
        assertEquals("Dogs", exception.getCategoryName());
        assertEquals(2, exception.getPetCount());
        assertTrue(exception.getMessage().contains("Cannot delete category 'Dogs'"));
        assertTrue(exception.getMessage().contains("2 pet(s)"));

        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteCategory_ReturnsFalse_WhenCategoryNotFound() {
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        
        boolean result = categoryService.deleteCategory(1L);

        
        assertFalse(result);
        verify(petRepository, never()).findByCategoryId(anyLong());
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetCategoryUsageCount() {
        
        when(petRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(testPet1, testPet2));

        
        int count = categoryService.getCategoryUsageCount(1L);

        
        assertEquals(2, count);
    }

    @Test
    void testCanDeleteCategory_ReturnsTrue_WhenNoPetsUsingCategory() {
        
        when(petRepository.findByCategoryId(1L)).thenReturn(Collections.emptyList());

        
        boolean canDelete = categoryService.canDeleteCategory(1L);

        
        assertTrue(canDelete);
    }

    @Test
    void testCanDeleteCategory_ReturnsFalse_WhenPetsUsingCategory() {
        
        when(petRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(testPet1));

        
        boolean canDelete = categoryService.canDeleteCategory(1L);

        
        assertFalse(canDelete);
    }

    @Test
    void testCategoryInUseException_CustomMessage() {

        CategoryInUseException exception = new CategoryInUseException(
            1L, 
            "Dogs", 
            3, 
            "Custom error message"
        );

        
        assertEquals(1L, exception.getCategoryId());
        assertEquals("Dogs", exception.getCategoryName());
        assertEquals(3, exception.getPetCount());
        assertEquals("Custom error message", exception.getMessage());
    }
}