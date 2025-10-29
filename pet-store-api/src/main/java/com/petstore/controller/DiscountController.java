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
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all discounts", description = "Retrieves all discounts.")
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get discount by ID", description = "Retrieve a discount by its ID.")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Long id) {

        Optional<Discount> discount = discountService.getDiscountById(id);
        return discount.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new discount.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create discount", description = "Create a new discount.")
    public ResponseEntity<Discount> createDiscount(@Valid @RequestBody Discount discount) {
        logger.debug("Creating new discount: {}", discount);
        // Discount discount = mapDtoToEntity(dto);
        Discount saved = discountService.saveDiscount(discount);
        return ResponseEntity.ok(saved);
    }

    /**
     * Update an existing discount.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update discount", description = "Update an existing discount.")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long id, @RequestBody Discount discount) {

        Discount updatedDiscount = discountService.updateDiscount(id, discount);

        if (updatedDiscount != null) {
            return ResponseEntity.ok(updatedDiscount);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete discount", description = "Delete a discount by its ID.")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {

        boolean deleted = discountService.deleteDiscount(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Validate a discount code from the UI before checkout.
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate discount code", description = "Validate a discount code from the UI before checkout.")
    public ResponseEntity<Discount> validateDiscount(@RequestParam String code) {
        Discount discount = discountService.validateDiscount(code); // throws InvalidDiscountException if invalid
        return ResponseEntity.ok(discount);
    }

    /**
     * List all available active discounts (for promo display in the UI).
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/active")
    @Operation(summary = "Get active discounts", description = "List all available active discounts for promo display in the UI.")
    public ResponseEntity<List<Discount>> getAvailableActiveDiscounts() {
        return ResponseEntity.ok(discountService.getAllActiveDiscounts());
    }

}
