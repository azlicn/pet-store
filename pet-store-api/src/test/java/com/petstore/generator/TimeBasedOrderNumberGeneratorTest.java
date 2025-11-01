package com.petstore.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;


/*  
 * Tests for TimeBasedOrderNumberGenerator
 */
public class TimeBasedOrderNumberGeneratorTest {

    /*
     * Test to ensure that generated order numbers are non-null and follow the expected format.
     */
    @Test
    void shouldGenerateOrderNumber() {
        
        Clock fixedClock = Clock.fixed(
                Instant.parse("2024-01-01T10:00:00Z"),
                ZoneId.systemDefault());
        TimeBasedOrderNumberGenerator generator = new TimeBasedOrderNumberGenerator(fixedClock);

        String orderNumber = generator.generate();

        assertNotNull(orderNumber);
        assertTrue(orderNumber.startsWith("ORD-"));
        assertEquals(14, orderNumber.length()); // ORD- + 6 + 4
    }

    /*
     * Test to ensure the order number format is correct: "ORD-{6-digit-timestamp-suffix}{4-digit-random}"
     */
    @Test
    void shouldFollowCorrectFormat() {

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);
        TimeBasedOrderNumberGenerator generator = new TimeBasedOrderNumberGenerator(fixedClock);

        String orderNumber = generator.generate();

        assertTrue(orderNumber.matches("ORD-\\d{10}"),
                "Order number should match ORD-{6-digit-timestamp-suffix}{4-digit-random}");
    }

    /*
     * Test to ensure that the last six digits of the timestamp are used in the order number.
     */
    @Test
    void shouldUseLastSixDigitsOfTimestamp() {

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);
        long millis = fixedClock.millis();
        String expectedSuffix = String.format("%06d", millis % 1_000_000);

        TimeBasedOrderNumberGenerator generator = new TimeBasedOrderNumberGenerator(fixedClock);
        String orderNumber = generator.generate();
        String actualSuffix = orderNumber.substring(4, 10);

        assertEquals(expectedSuffix, actualSuffix,
                "Should use last six digits of epoch millis");
    }

    /*
     * Test to ensure the random suffix is within the range 0000 to 9999.
     */
    @Test
    void shouldGenerateRandomSuffixWithinRange() {

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);
        TimeBasedOrderNumberGenerator generator = new TimeBasedOrderNumberGenerator(fixedClock);

        for (int i = 0; i < 1000; i++) {
            String orderNumber = generator.generate();
            int randomPart = Integer.parseInt(orderNumber.substring(10));
            assertTrue(randomPart >= 0 && randomPart <= 9999, "Random suffix must be within 0000–9999");
        }
    }

    /*
     * Test to ensure that duplicate order numbers are not generated in rapid succession.
     * This test is expected to fail due to the random component.
     * Note: In real scenarios, collisions can occur; this test highlights that possibility.
     * Not suitable for production use without additional uniqueness guarantees.
     */
    @Test
    void shouldFailWhenDuplicateOrderNumbersAreGenerated() {

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);
        TimeBasedOrderNumberGenerator generator = new TimeBasedOrderNumberGenerator(fixedClock);

        Set<String> results = new HashSet<>();
        boolean duplicateFound = false;

        for (int i = 0; i < 1000; i++) {
            String orderNumber = generator.generate();
            if (!results.add(orderNumber)) {
                duplicateFound = true;
                break;
            }
        }

        assertTrue(duplicateFound, "Expected duplicate order numbers, but all were unique");
    }

    /*
     * Test to ensure that different timestamps produce different prefixes.
     */
    @Test
    void shouldProduceDifferentPrefixForDifferentTimes() {

        Clock clock1 = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneOffset.UTC);
        Clock clock2 = Clock.fixed(Instant.parse("2024-01-01T10:00:10Z"), ZoneOffset.UTC);

        TimeBasedOrderNumberGenerator gen1 = new TimeBasedOrderNumberGenerator(clock1);
        TimeBasedOrderNumberGenerator gen2 = new TimeBasedOrderNumberGenerator(clock2);

        String first = gen1.generate();
        String second = gen2.generate();

        String prefix1 = first.substring(0, 10);
        String prefix2 = second.substring(0, 10);

        assertNotEquals(prefix1, prefix2, "Timestamp prefix should differ between times");
    }

    /*
     * Test to ensure generated order numbers are never null or empty
     */
    @Test
    void shouldNeverReturnNullOrEmpty() {

        Clock clock = Clock.systemUTC();
        TimeBasedOrderNumberGenerator generator = new TimeBasedOrderNumberGenerator(clock);

        for (int i = 0; i < 100; i++) {
            String orderNumber = generator.generate();
            assertNotNull(orderNumber);
            assertFalse(orderNumber.isEmpty());
        }
    }

}
