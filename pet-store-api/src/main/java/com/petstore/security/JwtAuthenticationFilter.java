package com.petstore.security;

import com.petstore.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A custom Spring Security filter that intercepts
 * every HTTP request once per request cycle to perform JWT (JSON Web Token) validation.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

     /**
     * Performs the actual JWT authentication filtering logic.
     *
     * @param request       the current HTTP request
     * @param response      the current HTTP response
     * @param filterChain   the filter chain to pass the request and response to the next filter
     * @throws ServletException if an error occurs while filtering
     * @throws IOException      if an input or output error occurs while filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.info("=== JWT Filter Debug ===");
        logger.info("Request URI: {}", request.getRequestURI());
        logger.info("Request Method: {}", request.getMethod());
        
        try {
            String jwt = getJwtFromRequest(request);
            logger.info("JWT Token present: {}", (jwt != null ? "YES" : "NO"));

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                logger.info("Username from token: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("User loaded: {}", userDetails.getUsername());
                logger.info("User authorities: {}", userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set in security context for user: {} with roles: {}", 
                    username, userDetails.getAuthorities());
            } else {
                logger.warn("JWT validation failed or token is null");
            }
        } catch (Exception ex) {
            logger.error("Exception in JWT filter: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
        logger.info("=== End JWT Filter Debug ===");
    }

    /**
     * Extracts the JWT token string from the {@code Authorization} header.
     * 
     * @param request the HTTP request from which to extract the token
     * @return the JWT token if present and properly formatted, otherwise {@code null}
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}