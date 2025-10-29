package com.petstore.controller;

import com.petstore.dto.LoginRequest;
import com.petstore.dto.SignUpRequest;
import com.petstore.exception.AuthenticationFailedException;
import com.petstore.exception.UserNotFoundException;
import com.petstore.model.Role;
import com.petstore.model.User;
import com.petstore.repository.UserRepository;
import com.petstore.security.JwtTokenProvider;
import com.petstore.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new AuthenticationFailedException("Invalid email or password");
        }

        String jwt = tokenProvider.generateToken(authentication);

        User user = userService.getUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");
        response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "roles", user.getRoles()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        Map<String, String> response = new HashMap<>();

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            response.put("message", "Email is already in use!");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName());

        if (signUpRequest.getRole() != null && signUpRequest.getRole().equals("ADMIN")) {
            user.setRoles(Set.of(Role.ADMIN));
        } else {
            user.setRoles(Set.of(Role.USER));
        }

        userService.saveUser(user);

        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }
}