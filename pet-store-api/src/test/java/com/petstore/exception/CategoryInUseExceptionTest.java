package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CategoryInUseException.
 */
@DisplayName("CategoryInUseException Tests")
class CategoryInUseExceptionTest {

    /**
     * Test creating CategoryInUseException with a category name and pet count.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with category name and pet count")
    void constructor_WithCategoryNameAndPetCount_ShouldCreateCorrectMessage() {
        String categoryName = "Dogs";
        int petCount = 2;
        String expectedMessage = String.format("Cannot delete category '%s' because it is being used by %d pet(s)", categoryName, petCount);
        CategoryInUseException exception = new CategoryInUseException(categoryName, petCount);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    /**
     * Test creating CategoryInUseException with a custom message.
     */
    @Test
    @DisplayName("Constructor - Should create correct message with custom message")
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
        String customMessage = "Custom error message";
        CategoryInUseException exception = new CategoryInUseException(customMessage, true);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}