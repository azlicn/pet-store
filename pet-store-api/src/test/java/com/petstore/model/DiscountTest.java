package com.petstore.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Discount Model Tests")
class DiscountTest {

    private Discount discount;
    private LocalDateTime now;
    private LocalDateTime tomorrow;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        tomorrow = now.plusDays(1);
        
        discount = new Discount();
        discount.setCode("SUMMER2025");
        discount.setPercentage(new BigDecimal("15.00"));
        discount.setValidFrom(now);
        discount.setValidTo(tomorrow);
        discount.setDescription("Summer sale discount");
        discount.setActive(true);
    }

    @Test
    @DisplayName("Should create discount with all fields")
    void shouldCreateDiscountWithAllFields() {
        // Assert
        assertThat(discount.getCode()).isEqualTo("SUMMER2025");
        assertThat(discount.getPercentage()).isEqualByComparingTo(new BigDecimal("15.00"));
        assertThat(discount.getValidFrom()).isEqualTo(now);
        assertThat(discount.getValidTo()).isEqualTo(tomorrow);
        assertThat(discount.getDescription()).isEqualTo("Summer sale discount");
        assertThat(discount.isActive()).isTrue();
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for ID")
    void gettersAndSetters_ShouldWorkForId() {
        // Act
        discount.setId(100L);

        // Assert
        assertThat(discount.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for code")
    void gettersAndSetters_ShouldWorkForCode() {
        // Act
        discount.setCode("WINTER2025");

        // Assert
        assertThat(discount.getCode()).isEqualTo("WINTER2025");
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for percentage")
    void gettersAndSetters_ShouldWorkForPercentage() {
        // Act
        discount.setPercentage(new BigDecimal("25.50"));

        // Assert
        assertThat(discount.getPercentage()).isEqualByComparingTo(new BigDecimal("25.50"));
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for validFrom")
    void gettersAndSetters_ShouldWorkForValidFrom() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.of(2025, 12, 1, 0, 0);

        // Act
        discount.setValidFrom(newDate);

        // Assert
        assertThat(discount.getValidFrom()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for validTo")
    void gettersAndSetters_ShouldWorkForValidTo() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.of(2025, 12, 31, 23, 59);

        // Act
        discount.setValidTo(newDate);

        // Assert
        assertThat(discount.getValidTo()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for description")
    void gettersAndSetters_ShouldWorkForDescription() {
        // Act
        discount.setDescription("Special holiday discount");

        // Assert
        assertThat(discount.getDescription()).isEqualTo("Special holiday discount");
    }

    @Test
    @DisplayName("Getters and Setters - Should work correctly for active status")
    void gettersAndSetters_ShouldWorkForActive() {
        // Act
        discount.setActive(false);

        // Assert
        assertThat(discount.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should handle null description")
    void shouldHandleNullDescription() {
        // Act
        discount.setDescription(null);

        // Assert
        assertThat(discount.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should default active to true")
    void shouldDefaultActiveToTrue() {
        // Arrange
        Discount newDiscount = new Discount();

        // Assert
        assertThat(newDiscount.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should handle small percentage values")
    void shouldHandleSmallPercentage() {
        // Act
        discount.setPercentage(new BigDecimal("0.50"));

        // Assert
        assertThat(discount.getPercentage()).isEqualByComparingTo(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("Should handle large percentage values")
    void shouldHandleLargePercentage() {
        // Act
        discount.setPercentage(new BigDecimal("99.99"));

        // Assert
        assertThat(discount.getPercentage()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    @DisplayName("Should handle zero percentage")
    void shouldHandleZeroPercentage() {
        // Act
        discount.setPercentage(BigDecimal.ZERO);

        // Assert
        assertThat(discount.getPercentage()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should support percentage with decimal places")
    void shouldSupportPercentageWithDecimals() {
        // Act
        discount.setPercentage(new BigDecimal("12.345"));

        // Assert
        assertThat(discount.getPercentage()).isEqualByComparingTo(new BigDecimal("12.345"));
    }

    @Test
    @DisplayName("Should handle short discount codes")
    void shouldHandleShortCodes() {
        // Act
        discount.setCode("X");

        // Assert
        assertThat(discount.getCode()).isEqualTo("X");
    }

    @Test
    @DisplayName("Should handle maximum length discount codes")
    void shouldHandleMaxLengthCodes() {
        // Arrange
        String maxLengthCode = "A".repeat(20);

        // Act
        discount.setCode(maxLengthCode);

        // Assert
        assertThat(discount.getCode()).hasSize(20);
        assertThat(discount.getCode()).isEqualTo(maxLengthCode);
    }

    @Test
    @DisplayName("Should handle codes with special characters")
    void shouldHandleCodesWithSpecialCharacters() {
        // Act
        discount.setCode("SAVE-20%");

        // Assert
        assertThat(discount.getCode()).isEqualTo("SAVE-20%");
    }

    @Test
    @DisplayName("Should handle codes with numbers")
    void shouldHandleCodesWithNumbers() {
        // Act
        discount.setCode("2025SALE");

        // Assert
        assertThat(discount.getCode()).isEqualTo("2025SALE");
    }

    @Test
    @DisplayName("Should handle long descriptions")
    void shouldHandleLongDescriptions() {
        // Arrange
        String longDescription = "This is a very long description that explains all the details about the discount and its terms and conditions for the special promotion period during holidays";

        // Act
        discount.setDescription(longDescription);

        // Assert
        assertThat(discount.getDescription()).isEqualTo(longDescription);
        assertThat(discount.getDescription().length()).isLessThanOrEqualTo(200);
    }

    @Test
    @DisplayName("Should handle maximum length description")
    void shouldHandleMaxLengthDescription() {
        // Arrange
        String maxLengthDescription = "A".repeat(200);

        // Act
        discount.setDescription(maxLengthDescription);

        // Assert
        assertThat(discount.getDescription()).hasSize(200);
    }

    @Test
    @DisplayName("Should support date range validation - validFrom before validTo")
    void shouldSupportValidDateRange() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);

        // Act
        discount.setValidFrom(start);
        discount.setValidTo(end);

        // Assert
        assertThat(discount.getValidFrom()).isBefore(discount.getValidTo());
    }

    @Test
    @DisplayName("Should support same day discount period")
    void shouldSupportSameDayDiscount() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2025, 6, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 15, 17, 0);

        // Act
        discount.setValidFrom(start);
        discount.setValidTo(end);

        // Assert
        assertThat(discount.getValidFrom().toLocalDate())
                .isEqualTo(discount.getValidTo().toLocalDate());
        assertThat(discount.getValidFrom()).isBefore(discount.getValidTo());
    }

    @Test
    @DisplayName("Should support long-term discount periods")
    void shouldSupportLongTermDiscount() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 31, 23, 59);

        // Act
        discount.setValidFrom(start);
        discount.setValidTo(end);

        // Assert
        assertThat(discount.getValidFrom()).isBefore(discount.getValidTo());
        assertThat(java.time.Duration.between(discount.getValidFrom(), discount.getValidTo()).toDays())
                .isGreaterThan(365);
    }

    @Test
    @DisplayName("Should have readable toString representation")
    void shouldHaveReadableToString() {
        // Arrange
        discount.setId(1L);

        // Act
        String toString = discount.toString();

        // Assert
        assertThat(toString).contains("Discount");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("code=SUMMER2025");
        assertThat(toString).contains("percentage=15.00");
        assertThat(toString).contains("active=true");
    }

    @Test
    @DisplayName("Should track creation timestamp")
    void shouldTrackCreationTimestamp() {
        // Assert - createdAt would be set by @PrePersist in actual database context
        // In unit test, we can verify the getter works
        assertThat(discount.getCreatedAt()).isNull(); // Not yet persisted
    }

    @Test
    @DisplayName("Should track update timestamp")
    void shouldTrackUpdateTimestamp() {
        // Assert - updatedAt would be set by @PreUpdate in actual database context
        // In unit test, we can verify the getter works
        assertThat(discount.getUpdatedAt()).isNull(); // Not yet persisted
    }

    @Test
    @DisplayName("Should support inactive discounts")
    void shouldSupportInactiveDiscounts() {
        // Act
        discount.setActive(false);

        // Assert
        assertThat(discount.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should support reactivating discounts")
    void shouldSupportReactivatingDiscounts() {
        // Arrange
        discount.setActive(false);

        // Act
        discount.setActive(true);

        // Assert
        assertThat(discount.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should support creating expired discount")
    void shouldSupportExpiredDiscount() {
        // Arrange
        LocalDateTime pastStart = LocalDateTime.now().minusDays(10);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(5);

        // Act
        discount.setValidFrom(pastStart);
        discount.setValidTo(pastEnd);

        // Assert
        assertThat(discount.getValidTo()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should support creating future discount")
    void shouldSupportFutureDiscount() {
        // Arrange
        LocalDateTime futureStart = LocalDateTime.now().plusDays(5);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(10);

        // Act
        discount.setValidFrom(futureStart);
        discount.setValidTo(futureEnd);

        // Assert
        assertThat(discount.getValidFrom()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should support discount code case sensitivity")
    void shouldSupportCodeCaseSensitivity() {
        // Act
        discount.setCode("SAVE20");
        Discount discount2 = new Discount();
        discount2.setCode("save20");

        // Assert
        assertThat(discount.getCode()).isNotEqualTo(discount2.getCode());
    }

    @Test
    @DisplayName("Should handle empty description")
    void shouldHandleEmptyDescription() {
        // Act
        discount.setDescription("");

        // Assert
        assertThat(discount.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("Should support flash sale scenarios")
    void shouldSupportFlashSale() {
        // Arrange - 1 hour flash sale
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        // Act
        discount.setCode("FLASH1HR");
        discount.setPercentage(new BigDecimal("50.00"));
        discount.setValidFrom(start);
        discount.setValidTo(end);
        discount.setDescription("1-hour flash sale - 50% off!");

        // Assert
        assertThat(discount.getCode()).isEqualTo("FLASH1HR");
        assertThat(discount.getPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(java.time.Duration.between(discount.getValidFrom(), discount.getValidTo()).toHours())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Should support seasonal discount scenarios")
    void shouldSupportSeasonalDiscount() {
        // Arrange - Winter season discount
        LocalDateTime winterStart = LocalDateTime.of(2025, 12, 1, 0, 0);
        LocalDateTime winterEnd = LocalDateTime.of(2026, 2, 28, 23, 59);

        // Act
        discount.setCode("WINTER2025");
        discount.setPercentage(new BigDecimal("20.00"));
        discount.setValidFrom(winterStart);
        discount.setValidTo(winterEnd);
        discount.setDescription("Winter season special offer");

        // Assert
        assertThat(discount.getCode()).contains("WINTER");
        assertThat(discount.getPercentage()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(discount.getDescription()).contains("Winter");
    }
}
