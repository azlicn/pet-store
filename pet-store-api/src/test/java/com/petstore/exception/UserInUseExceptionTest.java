package com.petstore.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserInUseExceptionTest {

    @Test
    void constructor_WithOwnedPetsOnly_ShouldCreateCorrectMessage() {
    String message = String.format("Cannot delete user '%s' (ID: %d) because they have ownership of %d pet(s) that still exist in the database", "user@test.com", 1L, 2);
    UserInUseException exception = new UserInUseException(message);
    assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_WithCreatedPetsOnly_ShouldCreateCorrectMessage() {
    String message = String.format("Cannot delete user '%s' (ID: %d) because they have created %d pet(s) that still exist in the database", "admin@test.com", 2L, 3);
    UserInUseException exception = new UserInUseException(message);
    assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_WithBothOwnedAndCreatedPets_ShouldCreateCorrectMessage() {
    String message = String.format("Cannot delete user '%s' (ID: %d) because they have ownership of %d pet(s) and created %d pet(s) that still exist in the database", "user2@test.com", 3L, 1, 2);
    UserInUseException exception = new UserInUseException(message);
    assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
    String customMessage = "Custom error message";
    UserInUseException exception = new UserInUseException(customMessage);
    assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void constructor_WithSingleOwnedPet_ShouldUseSingularForm() {
    String message = String.format("Cannot delete user '%s' (ID: %d) because they have ownership of %d pet(s) that still exist in the database", "user@test.com", 1L, 1);
    UserInUseException exception = new UserInUseException(message);
    assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_WithSingleCreatedPet_ShouldUseSingularForm() {
    String message = String.format("Cannot delete user '%s' (ID: %d) because they have created %d pet(s) that still exist in the database", "user@test.com", 1L, 1);
    UserInUseException exception = new UserInUseException(message);
    assertThat(exception.getMessage()).isEqualTo(message);
    }
}