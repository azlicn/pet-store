package com.petstore.controller;

import com.petstore.dto.UserUpdateRequest;
import com.petstore.exception.UserNotFoundException;
import com.petstore.model.User;
import com.petstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for user management operations.
 * Provides endpoints for retrieving, updating, and deleting users, as well as
 * converting user entities for response.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users. Only accessible by admins.
     *
     * @return ResponseEntity containing a list of user response maps
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve all users (ADMIN only)")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    /**
     * Retrieves a user by their ID. Admins can access any user; users can only
     * access their own.
     *
     * @param id the ID of the user to retrieve
     * @return ResponseEntity containing the user response map
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    @Operation(summary = "Get user by ID", description = "Retrieve user by ID (ADMIN can access any user, USER can only access their own)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(convertToUserResponse(user));
    }

    /**
     * Updates user information. Admins and users can update their own information;
     * only admins can update roles.
     *
     * @param id            the ID of the user to update
     * @param updateRequest the user update request data
     * @return ResponseEntity containing a success message and updated user response
     *         map
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    @Operation(summary = "Update user", description = "Update user information (ADMIN and USER can update)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest updateRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        User existingUser = userService.getUserById(id)
                .orElse(null);
        if (existingUser == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found with id: " + id);
            return ResponseEntity.status(404).body(error);
        }
        User userDetails = new User();
        userDetails.setFirstName(updateRequest.getFirstName());
        userDetails.setLastName(updateRequest.getLastName());
        userDetails.setEmail(updateRequest.getEmail());
        userDetails.setPhoneNumber(updateRequest.getPhoneNumber());
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
            userDetails.setPassword(updateRequest.getPassword());
        }
        // Only update roles if user is ADMIN and roles are provided
        // Otherwise, preserve existing roles
        if (isAdmin && updateRequest.getRoles() != null) {
            userDetails.setRoles(updateRequest.getRoles());
        } else {
            // Preserve existing roles for non-admin users or when roles not provided
            // Create a mutable copy to avoid UnsupportedOperationException during Hibernate merge
            userDetails.setRoles(new HashSet<>(existingUser.getRoles()));
        }
        User updatedUser = userService.updateUser(id, userDetails);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User updated successfully");
        response.put("user", convertToUserResponse(updatedUser));
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a user by their ID. Only admins can delete users. Users who own or
     * have created pets cannot be deleted until those pets are removed or
     * transferred.
     *
     * @param id the ID of the user to delete
     * @return ResponseEntity containing a success or error message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user by ID (ADMIN only). Users who own pets or have created pets cannot be deleted until those pets are removed or transferred.")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userService.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found with id: " + id);
                return ResponseEntity.badRequest().body(error);
            }
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to delete user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Converts a User entity to a response map, excluding sensitive information.
     *
     * @param user the User entity to convert
     * @return a map containing user details for response
     */
    private Map<String, Object> convertToUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("roles", user.getRoles());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());
        return response;
    }
}