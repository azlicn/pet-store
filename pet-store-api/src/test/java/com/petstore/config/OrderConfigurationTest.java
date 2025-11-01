package com.petstore.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.petstore.generator.OrderNumberGenerator;
import com.petstore.generator.SequentialOrderNumberGenerator;
import com.petstore.generator.TimeBasedOrderNumberGenerator;
import com.petstore.generator.UUIDOrderNumberGenerator;

/**
 * Unit tests for OrderConfiguration.
 * Tests the configuration of different order number generator strategies.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "SERVER_PORT=8080",
    "management.server.port=8081"
})
@DisplayName("OrderConfiguration Tests")
class OrderConfigurationTest {

    @Autowired
    private OrderNumberGenerator orderNumberGenerator;

    @Autowired
    private Clock clock;

    @Test
    @DisplayName("Should provide a Clock bean")
    void testClockBean() {
        assertThat(clock).isNotNull();
        assertThat(clock).isInstanceOf(Clock.class);
    }

    @Test
    @DisplayName("Should configure UUID generator by default")
    void testDefaultGenerator() {
        // Default should be UUID generator
        assertThat(orderNumberGenerator).isNotNull();
        assertThat(orderNumberGenerator).isInstanceOf(UUIDOrderNumberGenerator.class);
    }

    @Test
    @DisplayName("Should generate valid order numbers")
    void testGenerateOrderNumber() {
        String orderNumber = orderNumberGenerator.generate();
        
        assertThat(orderNumber).isNotNull();
        assertThat(orderNumber).isNotEmpty();
    }

    /**
     * Test configuration with sequential generator.
     */
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    @ActiveProfiles("test")
    @TestPropertySource(properties = {
        "order.generator.type=sequential",
        "SERVER_PORT=8080",
        "management.server.port=8081"
    })
    @DisplayName("OrderConfiguration with Sequential Generator")
    static class SequentialGeneratorTest {

        @Autowired
        private OrderNumberGenerator orderNumberGenerator;

        @Test
        @DisplayName("Should configure sequential generator when property is 'sequential'")
        void testSequentialGenerator() {
            assertThat(orderNumberGenerator).isNotNull();
            assertThat(orderNumberGenerator).isInstanceOf(SequentialOrderNumberGenerator.class);
        }

        @Test
        @DisplayName("Should generate sequential order numbers")
        void testSequentialOrderNumbers() {
            String orderNumber1 = orderNumberGenerator.generate();
            String orderNumber2 = orderNumberGenerator.generate();

            assertThat(orderNumber1).isNotNull();
            assertThat(orderNumber2).isNotNull();
            
            // Sequential numbers should have a pattern (though exact format depends on implementation)
            assertThat(orderNumber1).matches(".*\\d+.*");
            assertThat(orderNumber2).matches(".*\\d+.*");
        }
    }

    /**
     * Test configuration with time-based generator.
     */
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    @ActiveProfiles("test")
    @TestPropertySource(properties = {
        "order.generator.type=timebased",
        "SERVER_PORT=8080",
        "management.server.port=8081"
    })
    @DisplayName("OrderConfiguration with TimeBased Generator")
    static class TimeBasedGeneratorTest {

        @Autowired
        private OrderNumberGenerator orderNumberGenerator;

        @Test
        @DisplayName("Should configure time-based generator when property is 'timebased'")
        void testTimeBasedGenerator() {
            assertThat(orderNumberGenerator).isNotNull();
            assertThat(orderNumberGenerator).isInstanceOf(TimeBasedOrderNumberGenerator.class);
        }

        @Test
        @DisplayName("Should generate time-based order numbers")
        void testTimeBasedOrderNumbers() {
            String orderNumber = orderNumberGenerator.generate();

            assertThat(orderNumber).isNotNull();
            assertThat(orderNumber).isNotEmpty();
            
            // Time-based numbers should have a date/time pattern
            assertThat(orderNumber).matches(".*\\d+.*");
        }
    }

    /**
     * Test configuration with UUID generator explicitly set.
     */
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    @ActiveProfiles("test")
    @TestPropertySource(properties = {
        "order.generator.type=uuid",
        "SERVER_PORT=8080",
        "management.server.port=8081"
    })
    @DisplayName("OrderConfiguration with UUID Generator")
    static class UUIDGeneratorTest {

        @Autowired
        private OrderNumberGenerator orderNumberGenerator;

        @Test
        @DisplayName("Should configure UUID generator when property is 'uuid'")
        void testUUIDGenerator() {
            assertThat(orderNumberGenerator).isNotNull();
            assertThat(orderNumberGenerator).isInstanceOf(UUIDOrderNumberGenerator.class);
        }

        @Test
        @DisplayName("Should generate UUID-based order numbers")
        void testUUIDOrderNumbers() {
            String orderNumber = orderNumberGenerator.generate();

            assertThat(orderNumber).isNotNull();
            assertThat(orderNumber).isNotEmpty();
            
            // Should have ORD- prefix and hex characters
            assertThat(orderNumber).startsWith("ORD-");
            assertThat(orderNumber).matches("ORD-[A-F0-9]+");
        }

        @Test
        @DisplayName("Should generate unique order numbers")
        void testUniqueOrderNumbers() {
            String orderNumber1 = orderNumberGenerator.generate();
            String orderNumber2 = orderNumberGenerator.generate();

            assertThat(orderNumber1).isNotEqualTo(orderNumber2);
        }
    }

    /**
     * Test configuration with invalid/unknown generator type falls back to UUID.
     */
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    @ActiveProfiles("test")
    @TestPropertySource(properties = {
        "order.generator.type=invalid",
        "SERVER_PORT=8080",
        "management.server.port=8081"
    })
    @DisplayName("OrderConfiguration with Invalid Generator Type")
    static class InvalidGeneratorTest {

        @Autowired
        private OrderNumberGenerator orderNumberGenerator;

        @Test
        @DisplayName("Should fall back to UUID generator for invalid type")
        void testInvalidGeneratorFallback() {
            assertThat(orderNumberGenerator).isNotNull();
            assertThat(orderNumberGenerator).isInstanceOf(UUIDOrderNumberGenerator.class);
        }
    }

    /**
     * Test configuration with mixed case property values.
     */
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    @ActiveProfiles("test")
    @TestPropertySource(properties = {
        "order.generator.type=SEQUENTIAL",
        "SERVER_PORT=8080",
        "management.server.port=8081"
    })
    @DisplayName("OrderConfiguration with Case-Insensitive Properties")
    static class CaseInsensitiveTest {

        @Autowired
        private OrderNumberGenerator orderNumberGenerator;

        @Test
        @DisplayName("Should handle uppercase property values")
        void testUppercaseProperty() {
            assertThat(orderNumberGenerator).isNotNull();
            assertThat(orderNumberGenerator).isInstanceOf(SequentialOrderNumberGenerator.class);
        }
    }
}
