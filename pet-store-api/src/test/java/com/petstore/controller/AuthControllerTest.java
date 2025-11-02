package com.petstore.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.petstore.dto.LoginRequest;
import com.petstore.dto.SignUpRequest;
import com.petstore.enums.Role;
import com.petstore.model.User;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.petstore.security.JwtTokenProvider;
import com.petstore.service.UserService;
import com.petstore.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.config.TestSecurityConfig;
import com.petstore.service.UserDetailsServiceImpl;
import com.petstore.exception.AuthenticationFailedException;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * WebMvcTest for AuthController.
 * <p>
 * This test class covers authentication and registration endpoints, including positive, edge, and negative scenarios.
 * It validates controller behavior for valid requests, missing/invalid input, and error responses.
 * Security and exception handling are enabled for realistic test coverage.
 */
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * WebMvcTest for AuthController.
 * <p>
 * This test class covers authentication and registration endpoints, including
 * positive, edge, and negative scenarios.
 * It validates controller behavior for valid requests, missing/invalid input,
 * and error responses.
 * Security and exception handling are enabled for realistic test coverage.
 */
@WebMvcTest(AuthController.class)
@Import({ GlobalExceptionHandler.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Auth Controller WebMvcTest")
class AuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    /**
     * Test: POST /api/auth/login (success)
     * Verifies successful login returns JWT token and user details.
     */
    @Test
    @DisplayName("POST /api/auth/login - Success")
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password123");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(Set.of(Role.USER));
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    /**
     * Test: POST /api/auth/login (invalid credentials)
     * Verifies login with invalid credentials returns error response.
     */
    @Test
    @DisplayName("POST /api/auth/login - Invalid credentials")
    void shouldReturnErrorForInvalidCredentials() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("wrongpassword");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationFailedException("Invalid credentials"));
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test: POST /api/auth/register (success)
     * Verifies successful registration returns success message.
     */
    @Test
    @DisplayName("POST /api/auth/register - Success")
    void shouldRegisterUserSuccessfully() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("newuser@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setFirstName("New");
        signUpRequest.setLastName("User");
        signUpRequest.setRole("USER");
        when(userService.existsByEmail("newuser@example.com")).thenReturn(false);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(new User());
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    /**
     * Test: POST /api/auth/register (email already exists)
     * Verifies registration with existing email returns error response.
     */
    @Test
    @DisplayName("POST /api/auth/register - Email already exists")
    void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("existing@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setFirstName("Exist");
        signUpRequest.setLastName("User");
        signUpRequest.setRole("USER");
        when(userService.existsByEmail("existing@example.com")).thenReturn(true);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already in use!"));
    }

    /**
     * Test: POST /api/auth/register (invalid input)
     * Verifies registration with invalid input returns error response.
     */
    @Test
    @DisplayName("POST /api/auth/register - Invalid input")
    void shouldReturnErrorForInvalidInput() throws Exception {

        SignUpRequest signUpRequest = new SignUpRequest();

        signUpRequest.setEmail(""); // Invalid email
        signUpRequest.setPassword(""); // Invalid password
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test: POST /api/auth/register (email already exists)
     * Verifies registration with existing email returns error response.
     */
    @Test
    @DisplayName("POST /api/auth/login - Null request body")
    void shouldReturnErrorForNullLoginRequest() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test: POST /api/auth/login (missing email or password)
     * Verifies login with missing/empty fields returns error response.
     */
    @Test
    @DisplayName("POST /api/auth/login - Missing email or password")
    void shouldReturnErrorForMissingEmailOrPassword() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword("");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))

                .andExpect(status().is4xxClientError());
    }

    /**
     * Test: POST /api/auth/register (null request body)
     * Verifies registration with null request returns error response.
     */
    @Test
    @DisplayName("POST /api/auth/register - Null request body")
    void shouldReturnErrorForNullRegisterRequest() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Test: POST /api/auth/register (missing required fields)
     * Verifies registration with missing/empty fields returns error response.
     */
    @Test
    @DisplayName("POST /api/auth/register - Missing required fields")
    void shouldReturnErrorForMissingRequiredFields() throws Exception {

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("");
        signUpRequest.setPassword("");
        signUpRequest.setFirstName("");
        signUpRequest.setLastName("");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().is4xxClientError());
    }
}
