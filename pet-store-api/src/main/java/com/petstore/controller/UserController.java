package com.petstore.controller;

import com.petstore.dto.UserUpdateRequest;
import com.petstore.model.User;
import com.petstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve all users (ADMIN only)")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            
            List<Map<String, Object>> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());

            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to retrieve users: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    @Operation(summary = "Get user by ID", description = "Retrieve user by ID (ADMIN can access any user, USER can only access their own)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

            return ResponseEntity.ok(convertToUserResponse(user));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    @Operation(summary = "Update user", description = "Update user information (ADMIN and USER can update)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest updateRequest) {
        try {
            // Get current authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // Create user object from request
            User userDetails = new User();
            userDetails.setFirstName(updateRequest.getFirstName());
            userDetails.setLastName(updateRequest.getLastName());
            userDetails.setEmail(updateRequest.getEmail());
            
            // Only allow password updates if provided
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
                userDetails.setPassword(updateRequest.getPassword());
            }

            // Only ADMIN can update roles
            if (isAdmin && updateRequest.getRoles() != null) {
                userDetails.setRoles(updateRequest.getRoles());
            }

            User updatedUser = userService.updateUser(id, userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("user", convertToUserResponse(updatedUser));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", 
               description = "Delete user by ID (ADMIN only). Users who own pets or have created pets cannot be deleted until those pets are removed or transferred.")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // Check if user exists
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
     * Convert User entity to response format (excluding sensitive information)
     */
    private Map<String, Object> convertToUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("roles", user.getRoles());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());
        return response;
    }
}