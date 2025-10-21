package com.petstore.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserInUseExceptionTest {

    @Test
    void constructor_WithOwnedPetsOnly_ShouldCreateCorrectMessage() {
        UserInUseException exception = new UserInUseException(1L, "user@test.com", 2, 0);

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Cannot delete user 'user@test.com' (ID: 1) because they have ownership of 2 pet(s) that still exist in the database");
        assertThat(exception.getUserId()).isEqualTo(1L);
        assertThat(exception.getUserEmail()).isEqualTo("user@test.com");
        assertThat(exception.getOwnedPetCount()).isEqualTo(2);
        assertThat(exception.getCreatedPetCount()).isEqualTo(0);
        assertThat(exception.getTotalPetCount()).isEqualTo(2);
    }

    @Test
    void constructor_WithCreatedPetsOnly_ShouldCreateCorrectMessage() {
        UserInUseException exception = new UserInUseException(2L, "admin@test.com", 0, 3);

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Cannot delete user 'admin@test.com' (ID: 2) because they have created 3 pet(s) that still exist in the database");
        assertThat(exception.getUserId()).isEqualTo(2L);
        assertThat(exception.getUserEmail()).isEqualTo("admin@test.com");
        assertThat(exception.getOwnedPetCount()).isEqualTo(0);
        assertThat(exception.getCreatedPetCount()).isEqualTo(3);
        assertThat(exception.getTotalPetCount()).isEqualTo(3);
    }

    @Test
    void constructor_WithBothOwnedAndCreatedPets_ShouldCreateCorrectMessage() {
        UserInUseException exception = new UserInUseException(3L, "user2@test.com", 1, 2);

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Cannot delete user 'user2@test.com' (ID: 3) because they have ownership of 1 pet(s) and created 2 pet(s) that still exist in the database");
        assertThat(exception.getUserId()).isEqualTo(3L);
        assertThat(exception.getUserEmail()).isEqualTo("user2@test.com");
        assertThat(exception.getOwnedPetCount()).isEqualTo(1);
        assertThat(exception.getCreatedPetCount()).isEqualTo(2);
        assertThat(exception.getTotalPetCount()).isEqualTo(3);
    }

    @Test
    void constructor_WithCustomMessage_ShouldUseCustomMessage() {
        String customMessage = "Custom error message";
        UserInUseException exception = new UserInUseException(1L, "user@test.com", 1, 1, customMessage);

        assertThat(exception.getMessage()).isEqualTo(customMessage);
        assertThat(exception.getUserId()).isEqualTo(1L);
        assertThat(exception.getUserEmail()).isEqualTo("user@test.com");
        assertThat(exception.getOwnedPetCount()).isEqualTo(1);
        assertThat(exception.getCreatedPetCount()).isEqualTo(1);
        assertThat(exception.getTotalPetCount()).isEqualTo(2);
    }

    @Test
    void constructor_WithSingleOwnedPet_ShouldUseSingularForm() {
        UserInUseException exception = new UserInUseException(1L, "user@test.com", 1, 0);

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Cannot delete user 'user@test.com' (ID: 1) because they have ownership of 1 pet(s) that still exist in the database");
        assertThat(exception.getOwnedPetCount()).isEqualTo(1);
        assertThat(exception.getCreatedPetCount()).isEqualTo(0);
    }

    @Test
    void constructor_WithSingleCreatedPet_ShouldUseSingularForm() {
        UserInUseException exception = new UserInUseException(1L, "user@test.com", 0, 1);

        assertThat(exception.getMessage())
                .isEqualTo(
                        "Cannot delete user 'user@test.com' (ID: 1) because they have created 1 pet(s) that still exist in the database");
        assertThat(exception.getOwnedPetCount()).isEqualTo(0);
        assertThat(exception.getCreatedPetCount()).isEqualTo(1);
    }
}