package com.petstore.exception;

/**
 * Custom exception thrown when attempting to delete a user who has created or owns pets that still exist in the database
 */
public class UserInUseException extends RuntimeException {
    
    private final Long userId;
    private final String userEmail;
    private final int ownedPetCount;
    private final int createdPetCount;
    
    public UserInUseException(Long userId, String userEmail, int ownedPetCount, int createdPetCount) {
        super(buildMessage(userId, userEmail, ownedPetCount, createdPetCount));
        this.userId = userId;
        this.userEmail = userEmail;
        this.ownedPetCount = ownedPetCount;
        this.createdPetCount = createdPetCount;
    }
    
    public UserInUseException(Long userId, String userEmail, int ownedPetCount, int createdPetCount, String message) {
        super(message);
        this.userId = userId;
        this.userEmail = userEmail;
        this.ownedPetCount = ownedPetCount;
        this.createdPetCount = createdPetCount;
    }
    
    private static String buildMessage(Long userId, String userEmail, int ownedPetCount, int createdPetCount) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("Cannot delete user '%s' (ID: %d) because they have ", userEmail, userId));
        
        if (ownedPetCount > 0 && createdPetCount > 0) {
            message.append(String.format("ownership of %d pet(s) and created %d pet(s)", ownedPetCount, createdPetCount));
        } else if (ownedPetCount > 0) {
            message.append(String.format("ownership of %d pet(s)", ownedPetCount));
        } else if (createdPetCount > 0) {
            message.append(String.format("created %d pet(s)", createdPetCount));
        }
        
        message.append(" that still exist in the database");
        return message.toString();
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public int getOwnedPetCount() {
        return ownedPetCount;
    }
    
    public int getCreatedPetCount() {
        return createdPetCount;
    }
    
    public int getTotalPetCount() {
        return ownedPetCount + createdPetCount;
    }
}