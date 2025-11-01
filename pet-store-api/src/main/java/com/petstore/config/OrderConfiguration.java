package com.petstore.config;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.petstore.generator.SequentialOrderNumberGenerator;
import com.petstore.generator.TimeBasedOrderNumberGenerator;
import com.petstore.generator.UUIDOrderNumberGenerator;
import com.petstore.generator.OrderNumberGenerator;

@Configuration
public class OrderConfiguration {

    @Value("${order.generator.type:uuid}")
    private String generatorType;

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @Primary
    public OrderNumberGenerator configuredOrderNumberGenerator(
            UUIDOrderNumberGenerator uuidGenerator,
            SequentialOrderNumberGenerator sequentialGenerator,
            TimeBasedOrderNumberGenerator timeBasedGenerator) {

        return switch (generatorType.toLowerCase()) {
            case "sequential" -> sequentialGenerator;
            case "timebased" -> timeBasedGenerator;
            default -> uuidGenerator;
        };
    }

}
