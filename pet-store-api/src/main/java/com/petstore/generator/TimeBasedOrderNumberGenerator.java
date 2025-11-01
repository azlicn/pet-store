package com.petstore.generator;

import java.security.SecureRandom;
import java.time.Clock;

import org.springframework.stereotype.Component;

// Time-based Implementation with Dependency Injection
@Component("timeBasedOrderNumberGenerator")
public class TimeBasedOrderNumberGenerator implements OrderNumberGenerator {

    private final Clock clock;
    private final SecureRandom random;
    
    public TimeBasedOrderNumberGenerator(Clock clock) {
        this.clock = clock;
        this.random = new SecureRandom();
    }
    
    @Override
    public String generate() {
        
        String millis = String.valueOf(clock.millis());
        String last6 = millis.substring(Math.max(0, millis.length() - 6));
        last6 = String.format("%06d", Integer.parseInt(last6));
        
        int randomSuffix = random.nextInt(10000);
        String randomStr = String.format("%04d", randomSuffix);
        
        return "ORD-" + last6 + randomStr;
    }
}
