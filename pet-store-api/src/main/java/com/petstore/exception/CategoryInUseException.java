package com.petstore.exception;

/**
 * Custom exception thrown when attempting to delete a category that is still being used by pets
 */
public class CategoryInUseException extends RuntimeException {
    
    private final Long categoryId;
    private final String categoryName;
    private final int petCount;
    
    public CategoryInUseException(Long categoryId, String categoryName, int petCount) {
        super(String.format("Cannot delete category '%s' (ID: %d) because it is currently being used by %d pet(s)", 
                categoryName, categoryId, petCount));
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.petCount = petCount;
    }
    
    public CategoryInUseException(Long categoryId, String categoryName, int petCount, String message) {
        super(message);
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.petCount = petCount;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public int getPetCount() {
        return petCount;
    }
}