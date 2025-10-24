package com.petstore.controller;

import com.petstore.dto.LoginRequest;
import com.petstore.dto.SignUpRequest;
import com.petstore.exception.AuthenticationFailedException;
import com.petstore.exception.UserNotFoundException;
import com.petstore.model.Role;
import com.petstore.model.User;
import com.petstore.repository.UserRepository;
import com.petstore.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private LoginRequest loginRequest;
    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of(Role.USER));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("Jane");
        signUpRequest.setLastName("Smith");
        signUpRequest.setEmail("jane@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setRole("USER");
    }

    @Test
    @DisplayName("POST /api/auth/login - Should authenticate user successfully")
    void shouldAuthenticateUserSuccessfully() {

        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt-token-123";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        assertThat(responseBody.get("token")).isEqualTo(expectedToken);
        assertThat(responseBody.get("type")).isEqualTo("Bearer");
        assertThat(responseBody.get("user")).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) responseBody.get("user");
        assertThat(user.get("id")).isEqualTo(1L);
        assertThat(user.get("email")).isEqualTo("test@example.com");
        assertThat(user.get("firstName")).isEqualTo("John");
        assertThat(user.get("lastName")).isEqualTo("Doe");
        assertThat(user.get("roles")).isEqualTo(Set.of(Role.USER));
        assertThat(user).doesNotContainKey("password"); // Password should not be included
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return error for invalid credentials")
    void shouldReturnErrorForInvalidCredentials() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationFailedException("Invalid credentials"));

        assertThrows(AuthenticationFailedException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
    }

    @Test
    @DisplayName("POST /api/auth/login - Should handle user not found after authentication")
    void shouldHandleUserNotFoundAfterAuthentication() {

        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt-token-123";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
    }

    @Test
    @DisplayName("POST /api/auth/register - Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setEmail("jane@example.com");
        savedUser.setFirstName("Jane");
        savedUser.setLastName("Smith");
        savedUser.setRoles(Set.of(Role.USER));

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertThat(responseBody.get("message")).isEqualTo("User registered successfully!");
    }

    @Test
    @DisplayName("POST /api/auth/register - Should register admin user when role is ADMIN")
    void shouldRegisterAdminUserWhenRoleIsAdmin() {

        signUpRequest.setRole("ADMIN");
        signUpRequest.setEmail("admin@example.com");

        User savedAdminUser = new User();
        savedAdminUser.setId(3L);
        savedAdminUser.setEmail("admin@example.com");
        savedAdminUser.setFirstName("Jane");
        savedAdminUser.setLastName("Smith");
        savedAdminUser.setRoles(Set.of(Role.ADMIN));

        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedAdminUser);

        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertThat(responseBody.get("message")).isEqualTo("User registered successfully!");
    }

    @Test
    @DisplayName("POST /api/auth/register - Should default to USER role when role is null")
    void shouldDefaultToUserRoleWhenRoleIsNull() {

        signUpRequest.setRole(null);

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setEmail("jane@example.com");
        savedUser.setRoles(Set.of(Role.USER));

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST /api/auth/register - Should default to USER role for invalid role")
    void shouldDefaultToUserRoleForInvalidRole() {

        signUpRequest.setRole("INVALID_ROLE");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setEmail("jane@example.com");
        savedUser.setRoles(Set.of(Role.USER));

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return error when email already exists")
    void shouldReturnErrorWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signUpRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertThat(errorResponse.get("message")).isEqualTo("Email is already in use!");
    }

    @Test
    @DisplayName("LoginRequest - Should handle getters and setters correctly")
    void shouldHandleLoginRequestGettersAndSetters() {

        LoginRequest request = new LoginRequest();

        request.setEmail("test@example.com");
        request.setPassword("testPassword");

        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("testPassword");
    }

    @Test
    @DisplayName("SignUpRequest - Should handle getters and setters correctly")
    void shouldHandleSignUpRequestGettersAndSetters() {

        SignUpRequest request = new SignUpRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setRole("ADMIN");

        assertThat(request.getFirstName()).isEqualTo("John");
        assertThat(request.getLastName()).isEqualTo("Doe");
        assertThat(request.getEmail()).isEqualTo("john@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
        assertThat(request.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("POST /api/auth/login - Should handle authentication manager exception")
    void shouldHandleAuthenticationManagerException() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication service unavailable"));

        assertThrows(RuntimeException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
    }

    @Test
    @DisplayName("POST /api/auth/login - Should handle token generation failure")
    void shouldHandleTokenGenerationFailure() {

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication))
                .thenThrow(new RuntimeException("Token generation failed"));

        assertThrows(RuntimeException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
    }

    @Test
    @DisplayName("Edge cases - Should handle empty login credentials")
    void shouldHandleEmptyLoginCredentials() {

        LoginRequest emptyRequest = new LoginRequest();
        emptyRequest.setEmail("");
        emptyRequest.setPassword("");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationFailedException("Empty credentials"));

        assertThrows(AuthenticationFailedException.class, () -> {
            authController.authenticateUser(emptyRequest);
        });
    }

    @Test
    @DisplayName("Edge cases - Should handle null values in signup request")
    void shouldHandleNullValuesInSignupRequest() {

        SignUpRequest nullRequest = new SignUpRequest();
        nullRequest.setEmail("valid@example.com");
        nullRequest.setPassword("password123");

        when(userRepository.existsByEmail("valid@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = authController.registerUser(nullRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}