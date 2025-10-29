package com.petstore.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.model.Address;
import com.petstore.model.User;
import com.petstore.service.AddressService;
import com.petstore.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/users/addresses")
@Tag(name = "Address Controller", description = "Address Management API")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user addresses", description = "Retrieve all addresses for the authenticated user.")
    public ResponseEntity<List<Address>> getAddresses() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        List<Address> addresses = addressService.getUserAddresses(user.getId());

        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create address", description = "Create a new address for the authenticated user.")
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {

        logger.info("Creating address: {}", address.getFullAddress());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        Address savedAddress = addressService.createAddress(user.getId(), address);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update address", description = "Update an existing address for the authenticated user.")
    public ResponseEntity<Address> updateAddress(@PathVariable Long addressId, @RequestBody Address address) {

        Address updatedAddress = addressService.updateAddress(addressId, address);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete address", description = "Delete an address for the authenticated user.")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {

        addressService.deleteAddress(addressId);

        return ResponseEntity.noContent().build();
    }
}
