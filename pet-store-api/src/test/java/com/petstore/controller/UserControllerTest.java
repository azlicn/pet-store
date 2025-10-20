package com.petstore.controller;

import com.petstore.dto.UserUpdateRequest;
import com.petstore.model.Role;
import com.petstore.model.User;
import com.petstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.doThrow;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private User adminUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of(Role.USER));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Jane");
        adminUser.setLastName("Admin");
        adminUser.setPassword("encodedAdminPassword");
        adminUser.setRoles(Set.of(Role.ADMIN));
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());

        testUsers = Arrays.asList(testUser, adminUser);
    }

    @Test
    @DisplayName("GET /api/users - Should return all users for admin")
    void shouldReturnAllUsersForAdmin() {

        when(userService.getAllUsers()).thenReturn(testUsers);

        ResponseEntity<?> response = userController.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = (List<Map<String, Object>>) response.getBody();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).get("email")).isEqualTo("user@example.com");
        assertThat(users.get(0).get("password")).isNull(); 
        assertThat(users.get(1).get("email")).isEqualTo("admin@example.com");
    }

    @Test
    @DisplayName("GET /api/users - Should handle service exception")
    void shouldHandleServiceExceptionWhenGettingAllUsers() {

        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = userController.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertThat(errorResponse.get("message")).contains("Failed to retrieve users");
        assertThat(errorResponse.get("message")).contains("Database error");
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return user by ID when found")
    void shouldReturnUserByIdWhenFound() {

        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userController.getUserById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> userResponse = (Map<String, Object>) response.getBody();
        assertThat(userResponse.get("id")).isEqualTo(1L);
        assertThat(userResponse.get("email")).isEqualTo("user@example.com");
        assertThat(userResponse.get("firstName")).isEqualTo("John");
        assertThat(userResponse.get("lastName")).isEqualTo("Doe");
        assertThat(userResponse.get("password")).isNull();
        assertThat(userResponse.get("roles")).isEqualTo(Set.of(Role.USER));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return error when user not found")
    void shouldReturnErrorWhenUserNotFound() {

        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.getUserById(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertThat(errorResponse.get("message")).contains("User not found with id: 999");
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user successfully")
    void shouldUpdateUserSuccessfully() {

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPassword("newPassword");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setFirstName("John Updated");
        updatedUser.setLastName("Doe Updated");
        updatedUser.setRoles(Set.of(Role.USER));
        updatedUser.setCreatedAt(testUser.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenAnswer((Answer<Collection<GrantedAuthority>>) invocation -> Arrays
                    .asList(new SimpleGrantedAuthority("ROLE_USER")));

            ResponseEntity<?> response = userController.updateUser(1L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertThat(responseBody.get("message")).isEqualTo("User updated successfully");

            @SuppressWarnings("unchecked")
            Map<String, Object> userResponse = (Map<String, Object>) responseBody.get("user");
            assertThat(userResponse.get("firstName")).isEqualTo("John Updated");
            assertThat(userResponse.get("email")).isEqualTo("john.updated@example.com");
        }
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Admin should update user with roles")
    void adminShouldUpdateUserWithRoles() {

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Jane Updated");
        updateRequest.setLastName("Admin Updated");
        updateRequest.setEmail("jane.admin@example.com");
        updateRequest.setRoles(Set.of(Role.ADMIN, Role.USER));

        User updatedUser = new User();
        updatedUser.setId(2L);
        updatedUser.setEmail("jane.admin@example.com");
        updatedUser.setFirstName("Jane Updated");
        updatedUser.setLastName("Admin Updated");
        updatedUser.setRoles(Set.of(Role.ADMIN, Role.USER));
        updatedUser.setCreatedAt(adminUser.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenAnswer((Answer<Collection<GrantedAuthority>>) invocation -> Arrays
                    .asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

            when(userService.updateUser(eq(2L), any(User.class))).thenReturn(updatedUser);

            ResponseEntity<?> response = userController.updateUser(2L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertThat(responseBody.get("message")).isEqualTo("User updated successfully");

            @SuppressWarnings("unchecked")
            Map<String, Object> userResponse = (Map<String, Object>) responseBody.get("user");
            assertThat(userResponse.get("roles")).isEqualTo(Set.of(Role.ADMIN, Role.USER));
        }
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should handle update service exception")
    void shouldHandleUpdateServiceException() {

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("John");
        updateRequest.setEmail("invalid-email");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenAnswer((Answer<Collection<GrantedAuthority>>) invocation -> Arrays
                    .asList(new SimpleGrantedAuthority("ROLE_USER")));

            when(userService.updateUser(eq(1L), any(User.class)))
                    .thenThrow(new RuntimeException("Email already exists"));

            ResponseEntity<?> response = userController.updateUser(1L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isInstanceOf(Map.class);

            @SuppressWarnings("unchecked")
            Map<String, String> errorResponse = (Map<String, String>) response.getBody();
            assertThat(errorResponse.get("message")).contains("Failed to update user");
            assertThat(errorResponse.get("message")).contains("Email already exists");
        }
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should skip password update when empty")
    void shouldSkipPasswordUpdateWhenEmpty() {

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("John Updated");
        updateRequest.setPassword(""); 

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("John Updated");
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.setRoles(Set.of(Role.USER));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenAnswer((Answer<Collection<GrantedAuthority>>) invocation -> Arrays
                    .asList(new SimpleGrantedAuthority("ROLE_USER")));

            when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

            ResponseEntity<?> response = userController.updateUser(1L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user successfully")
    void shouldDeleteUserSuccessfully() {

        when(userService.existsById(1L)).thenReturn(true);

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertThat(responseBody.get("message")).isEqualTo("User deleted successfully");
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return error when user not found for deletion")
    void shouldReturnErrorWhenUserNotFoundForDeletion() {

        when(userService.existsById(999L)).thenReturn(false);

        ResponseEntity<?> response = userController.deleteUser(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertThat(errorResponse.get("message")).contains("User not found with id: 999");
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should handle delete service exception")
    void shouldHandleDeleteServiceException() {

        when(userService.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Cannot delete user with active pets")).when(userService).deleteUser(1L);

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertThat(errorResponse.get("message")).contains("Failed to delete user");
        assertThat(errorResponse.get("message")).contains("Cannot delete user with active pets");
    }

    @Test
    @DisplayName("convertToUserResponse - Should exclude sensitive information")
    void shouldExcludeSensitiveInformationInResponse() {

        ResponseEntity<?> response = userController.getUserById(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        response = userController.getUserById(1L);

        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> userResponse = (Map<String, Object>) response.getBody();
        
        assertThat(userResponse).containsKeys("id", "email", "firstName", "lastName", "roles", "createdAt", "updatedAt");
        
        assertThat(userResponse).doesNotContainKey("password");
    }

    @Test
    @DisplayName("UserUpdateRequest - Should handle null values properly")
    void shouldHandleNullValuesInUpdateRequest() {

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("John");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("John");
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.setRoles(Set.of(Role.USER));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenAnswer((Answer<Collection<GrantedAuthority>>) invocation -> Arrays
                    .asList(new SimpleGrantedAuthority("ROLE_USER")));

            when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

            ResponseEntity<?> response = userController.updateUser(1L, updateRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    @DisplayName("Edge cases - Should handle edge case IDs")
    void shouldHandleEdgeCaseIds() {

        when(userService.getUserById(0L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.getUserById(0L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        when(userService.existsById(0L)).thenReturn(false);
        ResponseEntity<?> deleteResponse = userController.deleteUser(0L);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Edge cases - Should handle negative IDs")
    void shouldHandleNegativeIds() {
        
        when(userService.getUserById(-1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userController.getUserById(-1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}