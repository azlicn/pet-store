package com.petstore.generator;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

// Timestamp + Atomic Counter Implementation
@Component("sequentialOrderNumberGenerator")
public class SequentialOrderNumberGenerator implements OrderNumberGenerator {

    private final AtomicLong counter = new AtomicLong(0);
    
    @Override
    public String generate() {
        // Thread-safe sequential generation
        long timestamp = Instant.now().getEpochSecond();
        long sequence = counter.incrementAndGet() % 100000; // Reset after 99999

        return String.format("ORD-%d-%05d", timestamp, sequence);
    }
}
