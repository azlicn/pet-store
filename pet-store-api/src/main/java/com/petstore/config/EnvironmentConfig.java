package com.petstore.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class EnvironmentConfig {
    
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
            
            System.out.println("✅ Environment variables loaded from .env file");
        } catch (Exception e) {
            System.out.println("⚠️  No .env file found or error loading it: " + e.getMessage());
            System.out.println("💡 Using default values from application.properties");
        }
    }
}