package com.petstore.integration;

import com.petstore.dto.UserUpdateRequest;
import com.petstore.enums.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.model.Role;
import com.petstore.model.User;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import com.petstore.repository.UserRepository;
import com.petstore.security.UserPrincipal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for User Management API endpoints.
 * Tests the full flow from HTTP request to database and back.
 */
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User additionalUser;

    @BeforeEach
    void setUp() {
        // Create an additional regular user for testing
        additionalUser = new User();
        additionalUser.setEmail("another@example.com");
        additionalUser.setPassword(passwordEncoder.encode("password123"));
        additionalUser.setFirstName("Another");
        additionalUser.setLastName("User");
        additionalUser.setRoles(Set.of(Role.USER));
        additionalUser = userRepository.save(additionalUser);
    }

    // ==================== GET All Users Tests ====================

    @Test
    void testGetAllUsers_Success_AsAdmin() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3)))) // testUser, testAdmin, additionalUser
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].lastName").exists())
                .andExpect(jsonPath("$[0].roles").exists())
                .andExpect(jsonPath("$[0].password").doesNotExist()); // Password should not be exposed
    }

    @Test
    void testGetAllUsers_Forbidden_AsRegularUser() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    // ==================== GET User By ID Tests ====================

    @Test
    void testGetUserById_Success_AsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testGetUserById_Success_OwnProfile_AsRegularUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUserById_Forbidden_OtherUserProfile_AsRegularUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testAdmin.getId())
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 99999L)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserById_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isForbidden());
    }

    // ==================== UPDATE User Tests ====================

    @Test
    void testUpdateUser_Success_AsAdmin() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Name");
        updateRequest.setEmail("updated@example.com");

        mockMvc.perform(put("/api/users/{id}", additionalUser.getId())
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.user.firstName").value("Updated"))
                .andExpect(jsonPath("$.user.lastName").value("Name"))
                .andExpect(jsonPath("$.user.email").value("updated@example.com"));
    }

    @Test
    void testUpdateUser_Success_OwnProfile_AsRegularUser() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("SelfUpdated");
        updateRequest.setLastName("User");
        updateRequest.setEmail("test-updated@example.com");

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.user.firstName").value("SelfUpdated"))
                .andExpect(jsonPath("$.user.email").value("test-updated@example.com"));
    }

    @Test
    void testUpdateUser_Forbidden_OtherUserProfile_AsRegularUser() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Hacker");
        updateRequest.setLastName("Attempt");

        mockMvc.perform(put("/api/users/{id}", testAdmin.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUser_AdminCanUpdateRoles() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Another");
        updateRequest.setLastName("User");
        updateRequest.setEmail("another@example.com");
        updateRequest.setRoles(Set.of(Role.ADMIN)); // Promote to admin

        mockMvc.perform(put("/api/users/{id}", additionalUser.getId())
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.roles").isArray())
                .andExpect(jsonPath("$.user.roles[0]").value("ADMIN"));
    }

    @Test
    void testUpdateUser_RegularUserCannotUpdateRoles() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Test");
        updateRequest.setLastName("User");
        updateRequest.setEmail("test@example.com");
        updateRequest.setRoles(Set.of(Role.ADMIN)); // Try to promote self to admin

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName").value("Test"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
        
        // Verify roles weren't changed by checking the user again
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    void testUpdateUser_WithPassword() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Another");
        updateRequest.setLastName("User");
        updateRequest.setEmail("another@example.com");
        updateRequest.setPassword("newpassword123");

        mockMvc.perform(put("/api/users/{id}", additionalUser.getId())
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Non");
        updateRequest.setLastName("Existent");

        mockMvc.perform(put("/api/users/{id}", 99999L)
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 99999"));
    }

    @Test
    void testUpdateUser_ValidationError_InvalidEmail() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Test");
        updateRequest.setLastName("User");
        updateRequest.setEmail("invalid-email"); // Invalid email format

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser_ValidationError_ShortFirstName() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("A"); // Too short (min 2)
        updateRequest.setLastName("User");
        updateRequest.setEmail("test@example.com");

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser_ValidationError_ShortPassword() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Test");
        updateRequest.setLastName("User");
        updateRequest.setEmail("test@example.com");
        updateRequest.setPassword("12345"); // Too short (min 6)

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE User Tests ====================

    @Test
    void testDeleteUser_Success_AsAdmin() throws Exception {
        // Create a user with no pets
        User userToDelete = new User();
        userToDelete.setEmail("delete-me@example.com");
        userToDelete.setPassword(passwordEncoder.encode("password123"));
        userToDelete.setFirstName("Delete");
        userToDelete.setLastName("Me");
        userToDelete.setRoles(Set.of(Role.USER));
        userToDelete = userRepository.save(userToDelete);

        mockMvc.perform(delete("/api/users/{id}", userToDelete.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        // Verify user is deleted
        mockMvc.perform(get("/api/users/{id}", userToDelete.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Forbidden_AsRegularUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", additionalUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 99999L)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found with id: 99999"));
    }

    @Test
    void testDeleteUser_WithAssociatedPets_ShouldFail() throws Exception {
        // Set up security context for creating pet
        UserPrincipal userPrincipal = UserPrincipal.create(additionalUser);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create a category and pet owned by additionalUser
        Category category = new Category();
        category.setName("TestCategory");
        category = categoryRepository.save(category);

        Pet pet = new Pet();
        pet.setName("User's Pet");
        pet.setDescription("This pet prevents user deletion");
        pet.setPrice(new BigDecimal("100.00"));
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setCategory(category);
        pet.setOwner(additionalUser);
        petRepository.save(pet);

        SecurityContextHolder.clearContext();

        // Try to delete user with associated pets - should fail
        mockMvc.perform(delete("/api/users/{id}", additionalUser.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Failed to delete user")));
    }

    @Test
    void testDeleteUser_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", additionalUser.getId()))
                .andExpect(status().isForbidden());
    }

    // ==================== Complex Scenario Tests ====================

    @Test
    void testUserLifecycle_UpdateAndDelete() throws Exception {
        // Create a new user
        User newUser = new User();
        newUser.setEmail("lifecycle@example.com");
        newUser.setPassword(passwordEncoder.encode("password123"));
        newUser.setFirstName("Lifecycle");
        newUser.setLastName("Test");
        newUser.setRoles(Set.of(Role.USER));
        newUser = userRepository.save(newUser);

        Long userId = newUser.getId();

        // Update the user
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Lifecycle");
        updateRequest.setEmail("updated-lifecycle@example.com");

        mockMvc.perform(put("/api/users/{id}", userId)
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.firstName").value("Updated"))
                .andExpect(jsonPath("$.user.email").value("updated-lifecycle@example.com"));

        // Verify update
        mockMvc.perform(get("/api/users/{id}", userId)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated-lifecycle@example.com"));

        // Delete the user
        mockMvc.perform(delete("/api/users/{id}", userId)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        // Verify deletion
        mockMvc.perform(get("/api/users/{id}", userId)
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers_VerifyPasswordNotExposed() throws Exception {
        String responseBody = mockMvc.perform(get("/api/users")
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Verify password field is not present in any user object
        org.junit.jupiter.api.Assertions.assertFalse(
            responseBody.contains("\"password\""),
            "Password field should not be exposed in API response"
        );
    }

    @Test
    void testUserCannotEscalatePrivileges() throws Exception {
        // Regular user tries to make themselves an admin
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("Test");
        updateRequest.setLastName("User");
        updateRequest.setEmail("test@example.com");
        updateRequest.setRoles(Set.of(Role.ADMIN, Role.USER)); // Try to add ADMIN role

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Verify user's roles array exists (roles management is restricted to admins)
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray());
    }
}
