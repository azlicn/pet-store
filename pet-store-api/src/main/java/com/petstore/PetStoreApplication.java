package com.petstore;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Pet Store API
 * Handles application startup and environment configuration
 */
@SpringBootApplication
public class PetStoreApplication {

    /**
     * Application entry point
     * Loads environment variables from .env file and starts the Spring application
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        
        // Load .env file if it exists
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();
        
        // Set system properties from .env file
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        
        SpringApplication.run(PetStoreApplication.class, args);
    }

}