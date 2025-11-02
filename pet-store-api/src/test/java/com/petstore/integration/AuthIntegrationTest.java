package com.petstore.integration;

import com.petstore.dto.LoginRequest;
import com.petstore.dto.SignUpRequest;
import com.petstore.enums.Role;
import com.petstore.model.User;
import com.petstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Authentication endpoints.
 * Tests login and registration functionality.
 */
@DisplayName("Authentication Integration Tests")
public class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User existingUser;

    @BeforeEach
    public void setUp() {
        // Create an existing user for login tests
        existingUser = new User(
                "existing@example.com",
                passwordEncoder.encode("password123"),
                "Existing",
                "User"
        );
        existingUser.setRoles(Set.of(Role.USER));
        existingUser = userRepository.save(existingUser);
    }

    // ==================== Login Tests ====================

    @Test
    @DisplayName("Should login successfully with valid credentials")
    public void testLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("existing@example.com", "password123");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.user.id").value(existingUser.getId()))
                .andExpect(jsonPath("$.user.email").value("existing@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("Existing"))
                .andExpect(jsonPath("$.user.lastName").value("User"))
                .andExpect(jsonPath("$.user.roles").isArray());
    }

    @Test
    @DisplayName("Should fail login with wrong password")
    public void testLogin_WrongPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("existing@example.com", "wrongpassword");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should fail login with non-existent email")
    public void testLogin_NonExistentEmail() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password123");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should fail login with empty email")
    public void testLogin_EmptyEmail() throws Exception {
        LoginRequest loginRequest = new LoginRequest("", "password123");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("email")));
    }

    @Test
    @DisplayName("Should fail login with invalid email format")
    public void testLogin_InvalidEmailFormat() throws Exception {
        LoginRequest loginRequest = new LoginRequest("invalid-email", "password123");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("email")));
    }

    @Test
    @DisplayName("Should fail login with empty password")
    public void testLogin_EmptyPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("existing@example.com", "");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("password")));
    }

    @Test
    @DisplayName("Should fail login with short password")
    public void testLogin_ShortPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("existing@example.com", "12345");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("password")));
    }

    @Test
    @DisplayName("Should fail login with null request body")
    public void testLogin_NullRequestBody() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        result.andExpect(status().isBadRequest());
    }

    // ==================== Registration Tests ====================

    @Test
    @DisplayName("Should register new user successfully")
    public void testRegister_Success() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "New",
                "User",
                "newuser@example.com",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Verify user was created in database
        User savedUser = userRepository.findByEmail("newuser@example.com").orElse(null);
        assert savedUser != null;
        assert savedUser.getFirstName().equals("New");
        assert savedUser.getLastName().equals("User");
        assert savedUser.getRoles().contains(Role.USER);
    }

    @Test
    @DisplayName("Should register new user with USER role explicitly")
    public void testRegister_WithUserRole() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Regular",
                "User",
                "regularuser@example.com",
                "password123",
                "USER"
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Verify user has USER role
        User savedUser = userRepository.findByEmail("regularuser@example.com").orElse(null);
        assert savedUser != null;
        assert savedUser.getRoles().contains(Role.USER);
        assert !savedUser.getRoles().contains(Role.ADMIN);
    }

    @Test
    @DisplayName("Should register new user with ADMIN role")
    public void testRegister_WithAdminRole() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Admin",
                "User",
                "adminuser@example.com",
                "password123",
                "ADMIN"
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Verify user has ADMIN role
        User savedUser = userRepository.findByEmail("adminuser@example.com").orElse(null);
        assert savedUser != null;
        assert savedUser.getRoles().contains(Role.ADMIN);
    }

    @Test
    @DisplayName("Should fail registration with duplicate email")
    public void testRegister_DuplicateEmail() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Duplicate",
                "User",
                "existing@example.com",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already in use!"));
    }

    @Test
    @DisplayName("Should fail registration with invalid email format")
    public void testRegister_InvalidEmailFormat() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Test",
                "User",
                "invalid-email",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("email")));
    }

    @Test
    @DisplayName("Should fail registration with empty first name")
    public void testRegister_EmptyFirstName() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "",
                "User",
                "test@example.com",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("firstName")));
    }

    @Test
    @DisplayName("Should fail registration with short first name")
    public void testRegister_ShortFirstName() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "A",
                "User",
                "test@example.com",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("firstName")));
    }

    @Test
    @DisplayName("Should fail registration with empty last name")
    public void testRegister_EmptyLastName() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Test",
                "",
                "test@example.com",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("lastName")));
    }

    @Test
    @DisplayName("Should fail registration with short last name")
    public void testRegister_ShortLastName() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Test",
                "U",
                "test@example.com",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("lastName")));
    }

    @Test
    @DisplayName("Should fail registration with empty password")
    public void testRegister_EmptyPassword() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Test",
                "User",
                "test@example.com",
                "",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("password")));
    }

    @Test
    @DisplayName("Should fail registration with short password")
    public void testRegister_ShortPassword() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Test",
                "User",
                "test@example.com",
                "12345",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("password")));
    }

    @Test
    @DisplayName("Should fail registration with invalid role")
    public void testRegister_InvalidRole() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "Test",
                "User",
                "test@example.com",
                "password123",
                "SUPERUSER"
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("role")));
    }

    // ==================== Integration/Workflow Tests ====================

    @Test
    @DisplayName("Should register and then login successfully")
    public void testRegisterThenLogin_Success() throws Exception {
        // Register a new user
        SignUpRequest signUpRequest = new SignUpRequest(
                "Register",
                "Login",
                "registerlogin@example.com",
                "password123",
                "USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Login with the newly registered user
        LoginRequest loginRequest = new LoginRequest("registerlogin@example.com", "password123");

        ResultActions loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        loginResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("registerlogin@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("Register"))
                .andExpect(jsonPath("$.user.lastName").value("Login"));
    }

    @Test
    @DisplayName("Should verify password is encrypted after registration")
    public void testRegister_PasswordEncryption() throws Exception {
        String plainPassword = "password123";
        SignUpRequest signUpRequest = new SignUpRequest(
                "Encrypt",
                "Test",
                "encrypt@example.com",
                plainPassword,
                null
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());

        // Verify password is encrypted (not stored in plain text)
        User savedUser = userRepository.findByEmail("encrypt@example.com").orElse(null);
        assert savedUser != null;
        assert !savedUser.getPassword().equals(plainPassword);
        assert passwordEncoder.matches(plainPassword, savedUser.getPassword());
    }

    @Test
    @DisplayName("Should handle multiple validation errors")
    public void testRegister_MultipleValidationErrors() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "A",  // Too short
                "B",  // Too short
                "invalid-email",  // Invalid format
                "123",  // Too short
                "INVALID_ROLE"  // Invalid role
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value(containsString("firstName")))
                .andExpect(jsonPath("$.message").value(containsString("lastName")))
                .andExpect(jsonPath("$.message").value(containsString("email")))
                .andExpect(jsonPath("$.message").value(containsString("password")))
                .andExpect(jsonPath("$.message").value(containsString("role")));
    }

    @Test
    @DisplayName("Should login with case-insensitive email")
    public void testLogin_CaseInsensitiveEmail() throws Exception {
        // Try to login with different case (should succeed since email is case-insensitive)
        LoginRequest loginRequest = new LoginRequest("EXISTING@EXAMPLE.COM", "password123");

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // This should succeed because email is case-insensitive
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("existing@example.com"));
    }

    @Test
    @DisplayName("Should register user with maximum valid name lengths")
    public void testRegister_MaxNameLengths() throws Exception {
        String maxLengthName = "a".repeat(50);  // 50 characters (max allowed)
        SignUpRequest signUpRequest = new SignUpRequest(
                maxLengthName,
                maxLengthName,
                "maxlength@example.com",
                "password123",
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("Should register user with maximum valid password length")
    public void testRegister_MaxPasswordLength() throws Exception {
        String maxLengthPassword = "a".repeat(100);  // 100 characters (max allowed)
        SignUpRequest signUpRequest = new SignUpRequest(
                "Test",
                "User",
                "maxpassword@example.com",
                maxLengthPassword,
                null
        );

        ResultActions result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }
}
