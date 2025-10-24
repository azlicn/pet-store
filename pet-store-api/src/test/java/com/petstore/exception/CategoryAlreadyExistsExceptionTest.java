package com.petstore.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CategoryAlreadyExistsException Tests")
class CategoryAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Constructor - Should create correct message with category name")
    void constructor_WithCategoryName_ShouldCreateCorrectMessage() {
        String categoryName = "Dogs";
        String expectedMessage = String.format("Category with name '%s' already exists", categoryName);
        CategoryAlreadyExistsException exception = new CategoryAlreadyExistsException(categoryName);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Constructor - Should create correct message with custom message")
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
        String customMessage = "Custom error message";
        CategoryAlreadyExistsException exception = new CategoryAlreadyExistsException(customMessage, true);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}