package com.petstore.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Test class for SequentialOrderNumberGenerator
 */
public class SequentialOrderNumberGeneratorTest {

    /*
     * Test to ensure generated order numbers are unique even under concurrent generation
     */
    @Test
    void sequentialGeneratorShouldBeThreadSafe() throws InterruptedException {
        // Given
        SequentialOrderNumberGenerator generator = new SequentialOrderNumberGenerator();
        Set<String> generatedNumbers = new HashSet<>();

        // When - Generate from multiple threads
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    synchronized (generatedNumbers) {
                        generatedNumbers.add(generator.generate());
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then - All should be unique despite concurrent generation
        assertEquals(10000, generatedNumbers.size());
    }

    /*
     * Test to ensure that the order number format matches "ORD-{timestamp}-{sequence}", where:
     * timestamp is a positive long (epoch seconds)
     * sequence is 5 digits, zero-padded
     */
    @Test
    void shouldGenerateCorrectFormat() {
        SequentialOrderNumberGenerator generator = new SequentialOrderNumberGenerator();
        String orderNumber = generator.generate();

        assertTrue(orderNumber.matches("ORD-\\d{10,}-\\d{5}"),
                "Order number should match ORD-{timestamp}-{5-digit-seq}");
    }

    /*
     * Test to ensure the sequence part increases properly for subsequent calls in the same second.
     */
    @Test
    void shouldIncrementSequenceSequentially() {
        SequentialOrderNumberGenerator generator = new SequentialOrderNumberGenerator();

        String first = generator.generate();
        String second = generator.generate();

        long seq1 = Long.parseLong(first.split("-")[2]);
        long seq2 = Long.parseLong(second.split("-")[2]);

        assertEquals(seq1 + 1, seq2, "Sequence should increment by 1 for each generation");
    }

    /*
     * Test to validate that sequence resets to 0 (or wraps around) after reaching 99,999 due to % 100000.
     */
    @Test
    void shouldResetSequenceAfterLimit() {
        SequentialOrderNumberGenerator generator = new SequentialOrderNumberGenerator();

        // simulate near wrap-around
        for (int i = 0; i < 99999; i++) {
            generator.generate();
        }
        String wrapped = generator.generate(); // should wrap back to 0

        long sequence = Long.parseLong(wrapped.split("-")[2]);
        assertEquals(0, sequence, "Sequence should reset to 0 after reaching 99999");
    }

    /*
     * Test to ensure that timestamp changes over time (it’s epoch seconds).
     */
    @Test
    void shouldIncludeCurrentTimestamp() throws InterruptedException {
        SequentialOrderNumberGenerator generator = new SequentialOrderNumberGenerator();

        String first = generator.generate();
        Thread.sleep(1100); // wait ~1 second
        String second = generator.generate();

        long t1 = Long.parseLong(first.split("-")[1]);
        long t2 = Long.parseLong(second.split("-")[1]);

        assertTrue(t2 >= t1, "Timestamp should be non-decreasing over time");
    }

    /*
     * Test to ensure generated order numbers are never null or empty
     */
    @Test
    void shouldNeverReturnNullOrEmpty() {
        SequentialOrderNumberGenerator generator = new SequentialOrderNumberGenerator();
        for (int i = 0; i < 1000; i++) {
            String orderNumber = generator.generate();
            assertNotNull(orderNumber);
            assertFalse(orderNumber.isEmpty());
        }
    }

    /*
     * Test to ensure generated order numbers have consistent length
     */
    @Test
    void shouldGenerateOrderNumbersWithConsistentLength() {
        SequentialOrderNumberGenerator generator = new SequentialOrderNumberGenerator();
        int expectedLength = generator.generate().length();

        for (int i = 0; i < 1000; i++) {
            assertEquals(expectedLength, generator.generate().length(),
                    "Order nu mber length should be consistent");
        }
    }

}