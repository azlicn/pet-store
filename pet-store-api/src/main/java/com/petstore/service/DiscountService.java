package com.petstore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.petstore.exception.DiscountAlreadyExistsException;
import com.petstore.exception.DiscountInUseException;
import com.petstore.exception.InvalidDiscountException;
import com.petstore.exception.DiscountNotFoundException;
import com.petstore.model.Discount;
import com.petstore.repository.DiscountRepository;

/**
 * Service for managing discounts in the store
 */
@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * Retrieves all discounts from the repository.
     *
     * @return list of all discounts
     */
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    /**
     * Retrieves a discount by its ID
     *
     * @param id the discount ID
     * @return the discount if found
     */
    public Discount getDiscountById(Long id) {

        return discountRepository.findById(id)
        .orElseThrow(() -> new DiscountNotFoundException(id));
    }

    /**
     * Saves a new discount to the repository.
     *
     * @param discount the discount to save
     * @return the saved discount
     */
    public Discount saveDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    /**
     * Updates an existing discount in the repository.
     *
     * @param discount the discount to update
     * @return the updated discount
     */
    public Discount updateDiscount(Long id, Discount discountDetails) {

        Discount existingDiscount = discountRepository.findById(id)
                .orElseThrow(() -> new DiscountNotFoundException(id));

        // Check for duplicate code (excluding current discount)
        if (discountRepository.existsByCodeAndIdNot(discountDetails.getCode(), id)) {
            throw new DiscountAlreadyExistsException(discountDetails.getCode());
        }

        existingDiscount.setCode(discountDetails.getCode());
        existingDiscount.setPercentage(discountDetails.getPercentage());
        existingDiscount.setValidFrom(discountDetails.getValidFrom());
        existingDiscount.setValidTo(discountDetails.getValidTo());
        existingDiscount.setDescription(discountDetails.getDescription());
        existingDiscount.setActive(discountDetails.isActive());

        return discountRepository.save(existingDiscount);
    }

    /**
     * Validates a discount code.
     *
     * @param code the discount code to validate
     * @return the valid discount
     * @throws InvalidDiscountException if the discount code is invalid or expired
     */
    public Discount validateDiscount(String code) {
        return discountRepository.findByCode(code)
                .filter(this::isWithinDateRange)
                .orElseThrow(() -> new InvalidDiscountException("Invalid or expired discount code"));
    }

    /**
     * Checks if the current date is within the discount's valid date range.
     *
     * @param discount the discount to check
     * @return true if within date range, false otherwise
     */
    private boolean isWithinDateRange(Discount discount) {
        LocalDateTime now = LocalDateTime.now();
        return (discount.getValidFrom() == null || !now.isBefore(discount.getValidFrom())) &&
                (discount.getValidTo() == null || !now.isAfter(discount.getValidTo()));
    }

    /**
     * Retrieves all active discounts.
     *
     * @return list of active discounts
     */
    public List<Discount> getAllActiveDiscounts() {
        return discountRepository.findAll()
                .stream()
                .filter(this::isWithinDateRange)
                .filter(Discount::isActive)
                .toList();
    }

    /**
     * Deletes a discount if it's not in use
     *
     * @param id the discount ID to delete
     */
    public void deleteDiscount(Long id) {

        if (!discountRepository.existsById(id)) {
            throw new DiscountNotFoundException(id);
        }
        try {
            discountRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DiscountInUseException(id);
        }
    }
}
