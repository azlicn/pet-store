package com.petstore.config;

import com.petstore.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * {@code SpringSecurityAuditorAware} provides the current auditor (user ID)
 * for Spring Data JPA auditing purposes.
 *
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

     /**
     * Returns the current auditor's user ID, if available.
     *
     * @return an {@link Optional} containing the current auditor's user ID,
     *         or {@code 1L} as the default system auditor
     */
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of(1L); // Default auditor ID for system operations
        }
        
        if (authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return Optional.of(userPrincipal.getId());
        }
        
        // Fallback for other authentication types
        return Optional.of(1L);
    }
}