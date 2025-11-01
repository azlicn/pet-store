package com.petstore.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.petstore.security.UserPrincipal;

/**
 * Unit tests for SpringSecurityAuditorAware.
 * Tests the auditor retrieval logic for various authentication scenarios.
 */
@DisplayName("SpringSecurityAuditorAware Tests")
class SpringSecurityAuditorAwareTest {

    private SpringSecurityAuditorAware auditorAware;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        auditorAware = new SpringSecurityAuditorAware();
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("When authentication is null")
    class NullAuthenticationTests {

        @Test
        @DisplayName("Should return default auditor ID (1L)")
        void testNullAuthentication() {
            // Given
            when(securityContext.getAuthentication()).thenReturn(null);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("When authentication is not authenticated")
    class NotAuthenticatedTests {

        @Test
        @DisplayName("Should return default auditor ID (1L)")
        void testNotAuthenticated() {
            // Given
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(false);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("When principal is anonymousUser")
    class AnonymousUserTests {

        @Test
        @DisplayName("Should return default auditor ID (1L)")
        void testAnonymousUser() {
            // Given
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("anonymousUser");
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("When principal is UserPrincipal")
    class UserPrincipalTests {

        @Test
        @DisplayName("Should return user ID from UserPrincipal")
        void testUserPrincipal() {
            // Given
            Long expectedUserId = 42L;
            UserPrincipal userPrincipal = mock(UserPrincipal.class);
            when(userPrincipal.getId()).thenReturn(expectedUserId);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(expectedUserId);
        }

        @Test
        @DisplayName("Should return different user IDs for different users")
        void testDifferentUserIds() {
            // Given - First user
            Long userId1 = 100L;
            UserPrincipal userPrincipal1 = mock(UserPrincipal.class);
            when(userPrincipal1.getId()).thenReturn(userId1);

            Authentication auth1 = new UsernamePasswordAuthenticationToken(
                userPrincipal1, null, userPrincipal1.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(auth1);

            // When - Get first auditor
            Optional<Long> auditor1 = auditorAware.getCurrentAuditor();

            // Given - Second user
            Long userId2 = 200L;
            UserPrincipal userPrincipal2 = mock(UserPrincipal.class);
            when(userPrincipal2.getId()).thenReturn(userId2);

            Authentication auth2 = new UsernamePasswordAuthenticationToken(
                userPrincipal2, null, userPrincipal2.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(auth2);

            // When - Get second auditor
            Optional<Long> auditor2 = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor1).isPresent();
            assertThat(auditor1.get()).isEqualTo(userId1);
            assertThat(auditor2).isPresent();
            assertThat(auditor2.get()).isEqualTo(userId2);
            assertThat(auditor1.get()).isNotEqualTo(auditor2.get());
        }

        @Test
        @DisplayName("Should handle user ID 1L correctly")
        void testUserIdOne() {
            // Given
            Long expectedUserId = 1L;
            UserPrincipal userPrincipal = mock(UserPrincipal.class);
            when(userPrincipal.getId()).thenReturn(expectedUserId);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("When principal is not UserPrincipal")
    class OtherPrincipalTests {

        @Test
        @DisplayName("Should return default auditor ID for String principal")
        void testStringPrincipal() {
            // Given
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("someUsername");
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return default auditor ID for custom principal")
        void testCustomPrincipal() {
            // Given
            Object customPrincipal = new Object();
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(customPrincipal);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("Optional handling")
    class OptionalHandlingTests {

        @Test
        @DisplayName("Should always return non-empty Optional")
        void testOptionalAlwaysPresent() {
            // Test with null authentication
            when(securityContext.getAuthentication()).thenReturn(null);
            Optional<Long> auditor1 = auditorAware.getCurrentAuditor();
            assertThat(auditor1).isPresent();

            // Test with UserPrincipal
            UserPrincipal userPrincipal = mock(UserPrincipal.class);
            when(userPrincipal.getId()).thenReturn(99L);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);
            Optional<Long> auditor2 = auditorAware.getCurrentAuditor();
            assertThat(auditor2).isPresent();

            // Test with anonymous user
            Authentication anonAuth = mock(Authentication.class);
            when(anonAuth.isAuthenticated()).thenReturn(true);
            when(anonAuth.getPrincipal()).thenReturn("anonymousUser");
            when(securityContext.getAuthentication()).thenReturn(anonAuth);
            Optional<Long> auditor3 = auditorAware.getCurrentAuditor();
            assertThat(auditor3).isPresent();
        }

        @Test
        @DisplayName("Should never return empty Optional")
        void testOptionalNeverEmpty() {
            // Given - various scenarios
            when(securityContext.getAuthentication()).thenReturn(null);
            
            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very large user IDs")
        void testLargeUserId() {
            // Given
            Long largeUserId = Long.MAX_VALUE;
            UserPrincipal userPrincipal = mock(UserPrincipal.class);
            when(userPrincipal.getId()).thenReturn(largeUserId);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("Should handle zero user ID")
        void testZeroUserId() {
            // Given
            Long zeroUserId = 0L;
            UserPrincipal userPrincipal = mock(UserPrincipal.class);
            when(userPrincipal.getId()).thenReturn(zeroUserId);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should handle authentication with null principal")
        void testNullPrincipal() {
            // Given
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(null);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // When
            Optional<Long> auditor = auditorAware.getCurrentAuditor();

            // Then
            assertThat(auditor).isPresent();
            assertThat(auditor.get()).isEqualTo(1L);
        }
    }
}
