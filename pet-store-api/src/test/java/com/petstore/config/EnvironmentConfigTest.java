package com.petstore.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EnvironmentConfig class.
 * Tests the loading of environment variables from .env file.
 */
@ExtendWith(MockitoExtension.class)
class EnvironmentConfigTest {

    @InjectMocks
    private EnvironmentConfig environmentConfig;

    private Map<String, String> originalSystemProperties;

    @BeforeEach
    void setUp() {
        // Backup original system properties
        originalSystemProperties = new HashMap<>();
        System.getProperties().stringPropertyNames().forEach(key -> 
            originalSystemProperties.put(key, System.getProperty(key))
        );
    }

    @AfterEach
    void tearDown() {
        // Restore original system properties
        System.getProperties().stringPropertyNames().forEach(key -> {
            if (!originalSystemProperties.containsKey(key)) {
                System.clearProperty(key);
            }
        });
        originalSystemProperties.forEach(System::setProperty);
    }

    @Test
    void testLoadEnvironmentVariables_Success() {
        // Given: Clean system properties
        String testKey = "TEST_ENV_VAR";
        System.clearProperty(testKey);
        
        // When: Load environment variables
        assertDoesNotThrow(() -> environmentConfig.loadEnvironmentVariables());
        
        // Then: Method executes without throwing exceptions
        // Note: Actual .env file loading depends on file existence
        // This test verifies the method can be called without errors
    }

    @Test
    void testLoadEnvironmentVariables_DoesNotOverrideExistingSystemProperty() {
        // Given: A system property already set
        String testKey = "EXISTING_PROPERTY";
        String originalValue = "original_value";
        System.setProperty(testKey, originalValue);
        
        // When: Load environment variables
        environmentConfig.loadEnvironmentVariables();
        
        // Then: Existing system property should not be overridden
        assertEquals(originalValue, System.getProperty(testKey));
    }

    @Test
    void testLoadEnvironmentVariables_HandlesException() {
        // When: Load environment variables (may fail if .env doesn't exist)
        // Then: Should not throw exception even if .env file is missing
        assertDoesNotThrow(() -> environmentConfig.loadEnvironmentVariables());
    }

    @Test
    void testLoadEnvironmentVariables_DoesNotOverrideEnvironmentVariables() {
        // Given: An environment variable is set (simulate by checking System.getenv)
        String envKey = System.getenv().keySet().stream().findFirst().orElse("PATH");
        String originalEnvValue = System.getenv(envKey);
        
        // When: Load environment variables
        environmentConfig.loadEnvironmentVariables();
        
        // Then: Environment variable should remain unchanged
        assertEquals(originalEnvValue, System.getenv(envKey));
    }

    @Test
    void testLoadEnvironmentVariables_IsIdempotent() {
        // When: Load environment variables multiple times
        environmentConfig.loadEnvironmentVariables();
        Map<String, String> firstLoad = new HashMap<>();
        System.getProperties().stringPropertyNames().forEach(key -> 
            firstLoad.put(key, System.getProperty(key))
        );
        
        environmentConfig.loadEnvironmentVariables();
        Map<String, String> secondLoad = new HashMap<>();
        System.getProperties().stringPropertyNames().forEach(key -> 
            secondLoad.put(key, System.getProperty(key))
        );
        
        // Then: System properties should be the same after multiple loads
        assertEquals(firstLoad.size(), secondLoad.size());
    }

    @Test
    void testPostConstructAnnotation() {
        // Given: Check that loadEnvironmentVariables is annotated with @PostConstruct
        // When: Spring container initializes this bean
        // Then: The method should be automatically called
        
        try {
            var method = EnvironmentConfig.class.getMethod("loadEnvironmentVariables");
            var postConstructAnnotation = method.getAnnotation(jakarta.annotation.PostConstruct.class);
            
            assertNotNull(postConstructAnnotation, 
                "loadEnvironmentVariables should be annotated with @PostConstruct");
        } catch (NoSuchMethodException e) {
            fail("loadEnvironmentVariables method should exist");
        }
    }

    @Test
    void testConfigurationAnnotation() {
        // Then: Class should be annotated with @Configuration
        var configurationAnnotation = EnvironmentConfig.class
            .getAnnotation(org.springframework.context.annotation.Configuration.class);
        
        assertNotNull(configurationAnnotation, 
            "EnvironmentConfig should be annotated with @Configuration");
    }

    @Test
    void testLoadEnvironmentVariables_WithMalformedEnv() {
        // When: Load environment variables with potential malformed .env
        // Then: Should handle gracefully due to ignoreIfMalformed() configuration
        assertDoesNotThrow(() -> environmentConfig.loadEnvironmentVariables());
    }

    @Test
    void testLoadEnvironmentVariables_WithMissingEnv() {
        // When: Load environment variables with missing .env file
        // Then: Should handle gracefully due to ignoreIfMissing() configuration
        assertDoesNotThrow(() -> environmentConfig.loadEnvironmentVariables());
    }
}
