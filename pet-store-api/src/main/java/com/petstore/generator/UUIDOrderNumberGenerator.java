package com.petstore.generator;

import java.util.UUID;

import org.springframework.stereotype.Component;

// UUID-based Implementation (Recommended for Production)
@Component("uuidOrderNumberGenerator")
public class UUIDOrderNumberGenerator implements OrderNumberGenerator {

    @Override
    public String generate() {

        // Generates truly unique order numbers using UUID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // Take first 10 characters for brevity
        return "ORD-" + uuid.substring(0, 10).toUpperCase();
        
    }

}
