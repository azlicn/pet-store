package com.petstore.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.petstore.repository")
@EntityScan(basePackages = "com.petstore.model")
public class TestJpaConfig {
}