package com.petstore.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.model.Discount;
import com.petstore.service.DiscountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

/**
 * REST controller for managing discounts in the store.
 * Provides endpoints for CRUD operations and validation of discounts.
 */
@RestController
@RequestMapping("/api/discounts")
@Tag(name = "Discount Controller", description = "Discount Management API")
public class DiscountController {

    private static final Logger logger = LoggerFactory.getLogger(DiscountController.class);

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    /**
     * Retrieves all discounts.
     *
     * @return ResponseEntity containing the list of all discounts
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all discounts", description = "Retrieves all discounts.")
    public ResponseEntity<List<Discount>> getAllDiscounts() {

        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    /**
     * Retrieves a discount by its ID.
     *
     * @param id the discount ID
     * @return ResponseEntity containing the discount if found, or not found status
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get discount by ID", description = "Retrieve a discount by its ID.")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Long id) {

        Discount discount = discountService.getDiscountById(id);

        return ResponseEntity.ok(discount);
    }

    /**
     * Creates a new discount.
     *
     * @param discount the discount to create
     * @return ResponseEntity containing the created discount
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create discount", description = "Create a new discount.")
    public ResponseEntity<Discount> createDiscount(@Valid @RequestBody Discount discount) {

        Discount saved = discountService.saveDiscount(discount);

        return ResponseEntity.ok(saved);
    }

    /**
     * Updates an existing discount.
     *
     * @param id       the discount ID to update
     * @param discount the updated discount data
     * @return ResponseEntity containing the updated discount if found, or not found
     *         status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update discount", description = "Update an existing discount.")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long id, @Valid @RequestBody Discount discount) {

        Discount updated = discountService.updateDiscount(id, discount);

        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a discount by its ID.
     *
     * @param id the discount ID to delete
     * @return ResponseEntity with no content if deleted, or not found status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete discount", description = "Delete a discount by its ID.")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {

        discountService.deleteDiscount(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Validates a discount code from the UI before checkout.
     *
     * @param code the discount code to validate
     * @return ResponseEntity containing the valid discount
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate discount code", description = "Validate a discount code from the UI before checkout.")
    public ResponseEntity<Discount> validateDiscount(@RequestParam String code) {
        Discount discount = discountService.validateDiscount(code); // throws InvalidDiscountException if invalid
        return ResponseEntity.ok(discount);
    }

    /**
     * Lists all available active discounts (for promo display in the UI).
     *
     * @return ResponseEntity containing the list of active discounts
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/active")
    @Operation(summary = "Get active discounts", description = "List all available active discounts for promo display in the UI.")
    public ResponseEntity<List<Discount>> getAvailableActiveDiscounts() {
        return ResponseEntity.ok(discountService.getAllActiveDiscounts());
    }

}
