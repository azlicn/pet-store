package com.petstore.config;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 *
 * Configuration class responsible for loading environment variables
 * from a <code>.env</code> file into the application context at startup.
 */
@Configuration
public class EnvironmentConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    
    /**
     * Loads environment variables from a <code>.env</code> file and registers them
     * as system properties, making them available to Spring's environment resolution.
     *
     */
    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            // Load .env file from classpath or current directory
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            // Set system properties so Spring can access them
            dotenv.entries().forEach(entry -> {
                // Only set if not already set by actual environment variables
                if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
            
            logger.debug("Environment variables loaded from .env file");
        } catch (Exception e) {
            logger.error("No .env file found or error loading it: " + e.getMessage());
            logger.error("Using default values from application.properties");
        }
    }
}