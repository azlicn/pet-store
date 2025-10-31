package com.petstore.service;

import com.petstore.exception.DiscountAlreadyExistsException;
import com.petstore.exception.DiscountNotFoundException;
import com.petstore.exception.InvalidDiscountException;
import com.petstore.model.Discount;
import com.petstore.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DiscountService} covering discount CRUD operations,
 * validation, and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Discount Service Tests")
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;
    @InjectMocks
    private DiscountService discountService;

    private Discount testDiscount;
    private Discount expiredDiscount;

    /**
     * Initializes test discounts before each test.
     */
    @BeforeEach
    void setUp() {

        testDiscount = new Discount();
        testDiscount.setId(1L);
        testDiscount.setCode("SAVE10");
        testDiscount.setPercentage(java.math.BigDecimal.valueOf(10));
        testDiscount.setValidFrom(LocalDateTime.now().minusDays(1));
        testDiscount.setValidTo(LocalDateTime.now().plusDays(1));
        testDiscount.setDescription("10% off");
        testDiscount.setActive(true);

        expiredDiscount = new Discount();
        expiredDiscount.setId(2L);
        expiredDiscount.setCode("OLD5");
        expiredDiscount.setPercentage(java.math.BigDecimal.valueOf(5));
        expiredDiscount.setValidFrom(LocalDateTime.now().minusDays(10));
        expiredDiscount.setValidTo(LocalDateTime.now().minusDays(5));
        expiredDiscount.setDescription("Expired");
        expiredDiscount.setActive(true);
    }

    /**
     * Tests retrieving all discounts.
     */
    @Test
    void getAllDiscounts_ShouldReturnDiscounts() {

        when(discountRepository.findAll()).thenReturn(Arrays.asList(testDiscount, expiredDiscount));
        List<Discount> discounts = discountService.getAllDiscounts();
        assertThat(discounts).hasSize(2).contains(testDiscount, expiredDiscount);
        verify(discountRepository).findAll();
    }

    /**
     * Tests retrieving a discount by ID.
     */
    @Test
    void getDiscountById_ShouldReturnDiscount() {

        when(discountRepository.findById(1L)).thenReturn(Optional.of(testDiscount));
        Discount result = discountService.getDiscountById(1L);
        assertThat(result).isEqualTo(testDiscount);
        verify(discountRepository).findById(1L);
    }

    /**
     * Tests retrieving a discount by ID when not found (edge case).
     */
    @Test
    void getDiscountById_NotFound_ShouldReturnEmpty() {

        when(discountRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> discountService.getDiscountById(999L))
                .isInstanceOf(DiscountNotFoundException.class);
        verify(discountRepository).findById(999L);
    }

    /**
     * Tests saving a new discount.
     */
    @Test
    void saveDiscount_ShouldSaveDiscount() {
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount);
        Discount saved = discountService.saveDiscount(testDiscount);
        assertThat(saved).isEqualTo(testDiscount);
        verify(discountRepository).save(testDiscount);
    }

    /**
     * Tests updating an existing discount.
     */
    @Test
    void updateDiscount_ShouldUpdateDiscount() {

        Discount details = new Discount();
        details.setCode("SAVE10");
        details.setPercentage(java.math.BigDecimal.valueOf(15));
        details.setValidFrom(LocalDateTime.now().minusDays(2));
        details.setValidTo(LocalDateTime.now().plusDays(2));
        details.setDescription("15% off");
        details.setActive(true);

        when(discountRepository.findById(1L)).thenReturn(Optional.of(testDiscount));
        when(discountRepository.existsByCodeAndIdNot("SAVE10", 1L)).thenReturn(false);
        when(discountRepository.save(any(Discount.class))).thenReturn(details);

        Discount updated = discountService.updateDiscount(1L, details);

        assertThat(updated.getPercentage()).isEqualTo(java.math.BigDecimal.valueOf(15));
        assertThat(updated.getDescription()).isEqualTo("15% off");
        verify(discountRepository).findById(1L);
        verify(discountRepository).existsByCodeAndIdNot("SAVE10", 1L);
        verify(discountRepository).save(testDiscount);
    }

    /**
     * Tests updating a discount with duplicate code (edge case).
     */
    @Test
    void updateDiscount_DuplicateCode_ShouldThrowException() {

        Discount details = new Discount();
        details.setCode("DUPLICATE");
        when(discountRepository.findById(1L)).thenReturn(Optional.of(testDiscount));
        when(discountRepository.existsByCodeAndIdNot("DUPLICATE", 1L)).thenReturn(true);

        testDiscount.setCode("SAVE10");
        assertThatThrownBy(() -> discountService.updateDiscount(1L, details))
                .isInstanceOf(DiscountAlreadyExistsException.class);
        verify(discountRepository).findById(1L);
        verify(discountRepository).existsByCodeAndIdNot("DUPLICATE", 1L);
        verify(discountRepository, never()).save(any(Discount.class));
    }

    /**
     * Tests updating a discount that does not exist (edge case).
     */
    @Test
    void updateDiscount_NotFound_ShouldReturnNull() {

        Discount details = new Discount();
        when(discountRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> discountService.updateDiscount(999L, details))
                .isInstanceOf(DiscountNotFoundException.class);
        verify(discountRepository).findById(999L);
        verify(discountRepository, never()).save(any(Discount.class));
    }

    /**
     * Tests validating a valid discount code.
     */
    @Test
    void validateDiscount_ValidCode_ShouldReturnDiscount() {

        when(discountRepository.findByCode("SAVE10")).thenReturn(Optional.of(testDiscount));
        Discount result = discountService.validateDiscount("SAVE10");
        assertThat(result).isEqualTo(testDiscount);
        verify(discountRepository).findByCode("SAVE10");
    }

    /**
     * Tests validating an invalid discount code (edge case).
     */
    @Test
    void validateDiscount_InvalidCode_ShouldThrowException() {

        when(discountRepository.findByCode("INVALID")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> discountService.validateDiscount("INVALID"))
                .isInstanceOf(InvalidDiscountException.class);
        verify(discountRepository).findByCode("INVALID");
    }

    /**
     * Tests validating an expired discount code (edge case).
     */
    @Test
    void validateDiscount_ExpiredCode_ShouldThrowException() {

        when(discountRepository.findByCode("OLD5")).thenReturn(Optional.of(expiredDiscount));
        assertThatThrownBy(() -> discountService.validateDiscount("OLD5"))
                .isInstanceOf(InvalidDiscountException.class);
        verify(discountRepository).findByCode("OLD5");
    }

    /**
     * Tests retrieving all active discounts.
     */
    @Test
    void getAllActiveDiscounts_ShouldReturnActiveDiscounts() {

        when(discountRepository.findAll()).thenReturn(Arrays.asList(testDiscount, expiredDiscount));
        List<Discount> active = discountService.getAllActiveDiscounts();
        assertThat(active).contains(testDiscount);
        assertThat(active).doesNotContain(expiredDiscount);
        verify(discountRepository).findAll();
    }

    /**
     * Tests deleting a discount successfully.
     */
    @Test
    void deleteDiscount_ShouldDeleteDiscount() {

        when(discountRepository.existsById(1L)).thenReturn(true);
        doNothing().when(discountRepository).deleteById(1L);
        discountService.deleteDiscount(1L);
        verify(discountRepository).existsById(1L);
        verify(discountRepository).deleteById(1L);
    }

    /**
     * Tests deleting a discount that does not exist (edge case).
     */
    @Test
    void deleteDiscount_NotFound_ShouldThrowException() {

        when(discountRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> discountService.deleteDiscount(999L))
                .isInstanceOf(DiscountNotFoundException.class);
        verify(discountRepository).existsById(999L);
        verify(discountRepository, never()).deleteById(any(Long.class));
    }
}
