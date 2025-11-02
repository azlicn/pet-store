package com.petstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.enums.Role;
import com.petstore.model.User;
import com.petstore.repository.UserRepository;
import com.petstore.security.JwtTokenProvider;
import com.petstore.security.UserPrincipal;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Base class for integration tests.
 * Provides common setup and utilities for testing with full Spring context.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    protected User testUser;
    protected User testAdmin;
    protected String userToken;
    protected String adminToken;

    @BeforeEach
    void baseSetUp() {
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Set.of(Role.USER));
        testUser = userRepository.save(testUser);
        
        // Create test admin
        testAdmin = new User();
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword(passwordEncoder.encode("admin123"));
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setRoles(Set.of(Role.ADMIN));
        testAdmin = userRepository.save(testAdmin);
        
        // Generate tokens
        userToken = generateToken(testUser);
        adminToken = generateToken(testAdmin);
    }

    /**
     * Helper method to generate JWT token for a user
     */
    protected String generateToken(User user) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities()
        );
        return jwtTokenProvider.generateToken(authentication);
    }

    /**
     * Helper method to create Authorization header with Bearer token
     */
    protected String createAuthorizationHeader(String token) {
        return "Bearer " + token;
    }
}
