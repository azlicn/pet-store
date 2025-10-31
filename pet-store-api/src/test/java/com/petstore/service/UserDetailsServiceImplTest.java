package com.petstore.service;

import com.petstore.model.User;
import com.petstore.repository.UserRepository;
import com.petstore.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserDetailsServiceImpl} covering user authentication loading and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    /**
     * Initializes a test user before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setPassword("password123");
    }

    /**
     * Tests loading user details by username (email) successfully.
     */
    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        UserDetails details = userDetailsService.loadUserByUsername("user@test.com");
        assertThat(details).isInstanceOf(UserPrincipal.class);
        assertThat(details.getUsername()).isEqualTo("user@test.com");
        verify(userRepository).findByEmail("user@test.com");
    }

    /**
     * Tests loading user details by username when user not found (edge case).
     */
    @Test
    void loadUserByUsername_UserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("notfound@test.com"))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("User not found with email: notfound@test.com");
        verify(userRepository).findByEmail("notfound@test.com");
    }

    /**
     * Tests loading user details by username with null email (edge case).
     */
    @Test
    void loadUserByUsername_NullEmail_ShouldThrowException() {
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("User not found with email: null");
        verify(userRepository).findByEmail(null);
    }

    /**
     * Tests loading user details by username with empty email (edge case).
     */
    @Test
    void loadUserByUsername_EmptyEmail_ShouldThrowException() {
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("User not found with email: ");
        verify(userRepository).findByEmail("");
    }
}
