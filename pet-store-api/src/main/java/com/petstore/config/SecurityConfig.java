package com.petstore.config;

import com.petstore.security.JwtAuthenticationFilter;
import com.petstore.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Actuator endpoints - require ADMIN role for security
                .requestMatchers("/actuator/health").permitAll()                          // Health endpoint public
                .requestMatchers("/actuator/**").hasRole("ADMIN")                         // Other actuator endpoints for ADMIN only
                
                // Public read access for browsing pets and categories
                .requestMatchers("GET", "/api/pets").permitAll()                           // Public pet list and filtering
                .requestMatchers("GET", "/api/pets/latest").permitAll()                    // Public latest pets for home page
                .requestMatchers("GET", "/api/categories").permitAll()                     // Public categories list
                
                // Authenticated access for pet details and operations
                .requestMatchers("GET", "/api/pets/*").hasAnyRole("USER", "ADMIN")         // Pet details require login
                .requestMatchers("GET", "/api/pets/my-pets").hasAnyRole("USER", "ADMIN")   // User's purchased pets
                .requestMatchers("POST", "/api/pets/*/purchase").hasAnyRole("USER", "ADMIN") // Pet purchase for users
                .requestMatchers("POST", "/api/pets").hasAnyRole("USER", "ADMIN")          // USER and ADMIN can add pets
                .requestMatchers("PUT", "/api/pets/**").hasAnyRole("USER", "ADMIN")        // USER and ADMIN can update pets  
                .requestMatchers("DELETE", "/api/pets/**").hasRole("ADMIN")                // Only ADMIN can delete pets
                .requestMatchers("POST", "/api/categories/**").hasRole("ADMIN")            // Only ADMIN can create
                .requestMatchers("PUT", "/api/categories/**").hasRole("ADMIN")             // Only ADMIN can update
                .requestMatchers("DELETE", "/api/categories/**").hasRole("ADMIN")          // Only ADMIN can delete
                
                // User management endpoints - rely on @PreAuthorize for fine-grained control
                .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")              // User endpoints require authentication
                
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}