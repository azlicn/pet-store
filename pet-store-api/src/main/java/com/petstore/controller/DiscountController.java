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
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/discounts")
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
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<Discount> createDiscount(@Valid @RequestBody Discount discount) {
        logger.debug("Creating new discount: {}", discount);
        //Discount discount = mapDtoToEntity(dto);
        Discount saved = discountService.saveDiscount(discount);
        return ResponseEntity.ok(saved);
    }

    /**
     * Update an existing discount.
     */
    @PreAuthorize("hasRole('ADMIN')")
     @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long id, @RequestBody Discount discount) {

        Discount updatedDiscount = discountService.updateDiscount(id, discount);

        if (updatedDiscount != null) {
            return ResponseEntity.ok(updatedDiscount);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<Discount> validateDiscount(@RequestParam String code) {
        Discount discount = discountService.validateDiscount(code); // throws InvalidDiscountException if invalid
        return ResponseEntity.ok(discount);
    }

    /**
     * List all available active discounts (for promo display in the UI).
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<Discount>> getAvailableActiveDiscounts() {
        return ResponseEntity.ok(discountService.getAllActiveDiscounts());
    }

}
