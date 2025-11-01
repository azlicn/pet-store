package com.petstore.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/*
 * Unit tests for UUIDOrderNumberGenerator
 */
public class UUIDOrderNumberGeneratorTest {

    /*
     * Test to ensure generated order numbers are unique
     */
    @Test
    void shouldGenerateUniqueNumbers() {
        // Given
        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        Set<String> generatedNumbers = new HashSet<>();

        // When - Generate 10000 order numbers
        for (int i = 0; i < 10000; i++) {
            String orderNumber = generator.generate();
            generatedNumbers.add(orderNumber);
        }

        // Then - All should be unique
        assertEquals(10000, generatedNumbers.size());
    }

    /*
     * Test to verify the format of generated order numbers
     */
    @Test
    void shouldGenerateOrderNumberWithCorrectFormat() {

        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        String orderNumber = generator.generate();

        assertTrue(orderNumber.startsWith("ORD-"), "Order number should start with ORD-");
        String suffix = orderNumber.substring(4);

        assertEquals(10, suffix.length(), "Suffix should be 10 characters long");
        assertTrue(suffix.matches("[A-Z0-9]+"), "Suffix should contain only uppercase letters and digits");
    }

    /*
     * Test to ensure generated order numbers are uppercase
     */
    @Test
    void shouldGenerateUppercaseOrderNumbers() {
        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        String orderNumber = generator.generate();

        assertEquals(orderNumber, orderNumber.toUpperCase(), "Order number should be uppercase");
    }

    /*
     * Test to ensure generated order numbers always start with "ORD-"
     */
    @Test
    void shouldAlwaysStartWithOrdPrefix() {
        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        for (int i = 0; i < 100; i++) {
            assertTrue(generator.generate().startsWith("ORD-"));
        }
    }

    /*
     * Test to ensure generated order numbers have consistent length
     */
    @Test
    void shouldGenerateOrderNumbersWithConsistentLength() {
        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        int expectedLength = generator.generate().length();
        for (int i = 0; i < 1000; i++) {
            assertEquals(expectedLength, generator.generate().length());
        }
    }

    /*
     * Performance test to ensure generation is efficient
     */
    @Test
    void shouldGenerateOrderNumbersQuickly() {
        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            generator.generate();
        }
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 2000, "Should generate 100k order numbers under 2 seconds");
    }

    /*
     * Test to ensure generated order numbers are never null or empty
     */
    @Test
    void shouldNeverReturnNullOrEmpty() {
        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        for (int i = 0; i < 1000; i++) {
            String orderNumber = generator.generate();
            assertNotNull(orderNumber);
            assertFalse(orderNumber.isEmpty());
        }
    }

    /*
     * Test to ensure high uniqueness over multiple runs
     */
    @Test
    void shouldMaintainHighUniquenessOverMultipleRuns() {
        UUIDOrderNumberGenerator generator = new UUIDOrderNumberGenerator();
        Set<String> generated = new HashSet<>();
        int total = 1_000_000; // 1 million
        for (int i = 0; i < total; i++) {
            generated.add(generator.generate());
        }
        double uniquenessRate = (generated.size() * 1.0 / total) * 100;
        assertTrue(uniquenessRate > 99.999, "Uniqueness rate should exceed 99.999%");
    }

}
