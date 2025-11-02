package com.petstore.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.petstore.model.User;
import com.petstore.security.JwtTokenProvider;
import com.petstore.service.UserService;
import com.petstore.service.UserDetailsServiceImpl;

import com.petstore.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.config.TestSecurityConfig;
import com.petstore.enums.Role;

/**
 * WebMvcTest for UserController.
 * <p>
 * This test class covers all user management endpoints, including retrieval,
 * update, and deletion of users.
 * It validates positive scenarios (successful operations) and key edge/negative
 * cases (not found, error responses).
 * Security filters and exception handling are enabled for realistic access
 * control and error simulation.
 */
@WebMvcTest(UserController.class)
@Import({ GlobalExceptionHandler.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("User Controller WebMvcTest")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test: GET /api/users (ADMIN only)
     * Verifies all users are returned for admin.
     */
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void shouldReturnAllUsersForAdmin() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@test.com");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setRoles(Set.of(Role.ADMIN));
        when(userService.getAllUsers()).thenReturn(List.of(user));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email")
                        .value("admin@test.com"));

    }

    /**
     * Test: GET /api/users/{id} (ADMIN)
     * Verifies user is returned by ID for admin.
     */
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void shouldReturnUserByIdForAdmin() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(Set.of(Role.USER));
        when(userService.getUserById(2L)).thenReturn(Optional.of(user));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value("user@test.com"));
    }

    /**
     * Test: GET /api/users/{id} (ADMIN) - Not Found
     * Verifies 404 is returned if user not found.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404IfUserNotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User not found with email: 'User not found with id: 99'"));
    }

    /**
     * Test: PUT /api/users/{id} (ADMIN)
     * Verifies user is updated successfully by admin.
     */
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void shouldUpdateUserByAdmin() throws Exception {
        com.petstore.dto.UserUpdateRequest updateRequest = new com.petstore.dto.UserUpdateRequest("Test", "User",
                "user@test.com", "1234567890", "password123", Set.of(Role.USER));
        User updatedUser = new User();
        updatedUser.setId(2L);
        updatedUser.setEmail("user@test.com");
        updatedUser.setFirstName("Test");
        updatedUser.setLastName("User");
        updatedUser.setPhoneNumber("1234567890");
        updatedUser.setRoles(Set.of(Role.USER));
        when(userService.getUserById(2L)).thenReturn(Optional.of(updatedUser));
        when(userService.updateUser(eq(2L), any(User.class))).thenReturn(updatedUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/2")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User updated successfully"));
    }

    /**
     * Test: PUT /api/users/{id} (ADMIN) - Not Found
     * Verifies 404 is returned if user to update does not exist.
     */
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUpdatingNonexistentUser() throws Exception {
        com.petstore.dto.UserUpdateRequest updateRequest = new com.petstore.dto.UserUpdateRequest("Test", "User",
                "user@test.com", "1234567890", "password123", Set.of(Role.USER));
        when(userService.getUserById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/99")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User not found with id: 99"));
    }

    /**
     * Test: DELETE /api/users/{id} (ADMIN)
     * Verifies user is deleted successfully by admin.
     */
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void shouldDeleteUserByAdmin() throws Exception {
        when(userService.existsById(2L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User deleted successfully"));
    }

    /**
     * Test: DELETE /api/users/{id} (ADMIN) - Not Found
     * Verifies error if user to delete does not exist.
     */
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void shouldReturnErrorWhenDeletingNonexistentUser() throws Exception {
        when(userService.existsById(99L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/99"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User not found with id: 99"));
    }

    /**
     * Test: DELETE /api/users/{id} (ADMIN) - Exception
     * Verifies error if service throws exception during delete.
     */
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void shouldReturnErrorWhenDeleteThrowsException() throws Exception {
        when(userService.existsById(2L)).thenReturn(true);
        org.mockito.Mockito.doThrow(new RuntimeException("Cannot delete user")).when(userService).deleteUser(2L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/2"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Failed to delete user: Cannot delete user"));
    }

}