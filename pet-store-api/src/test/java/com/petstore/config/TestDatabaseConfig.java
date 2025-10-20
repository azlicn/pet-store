package com.petstore.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Test configuration for H2 database setup
 */
@TestConfiguration
@Profile("test")
@Order(1)
public class TestDatabaseConfig {

    @Bean
    public DatabaseInitializer databaseInitializer(DataSource dataSource) {
        return new DatabaseInitializer(dataSource);
    }

    public static class DatabaseInitializer {
        private final DataSource dataSource;

        public DatabaseInitializer(DataSource dataSource) {
            this.dataSource = dataSource;
            initializeDatabase();
        }

        private void initializeDatabase() {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            try {
                // Create tables if they don't exist
                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL
                    )
                """);

                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        first_name VARCHAR(255) NOT NULL,
                        last_name VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        phone VARCHAR(255),
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL
                    )
                """);

                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS pets (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        category_id BIGINT,
                        status VARCHAR(50) NOT NULL,
                        photo_urls TEXT,
                        user_id BIGINT,
                        price DECIMAL(10,2) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        created_by BIGINT,
                        last_modified_by BIGINT,
                        FOREIGN KEY (category_id) REFERENCES categories(id),
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    )
                """);

                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS user_roles (
                        user_id BIGINT NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    )
                """);

                // Create pet_photos table for pet photo URLs collection
                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS pet_photos (
                        pet_id BIGINT NOT NULL,
                        photo_url VARCHAR(500),
                        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
                    )
                """);

                // Create pet_tags table for pet tags collection
                jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS pet_tags (
                        pet_id BIGINT NOT NULL,
                        tag VARCHAR(255),
                        FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
                    )
                """);

                // Create indexes for better performance
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pets_status ON pets(status)");
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pets_owner ON pets(user_id)");
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pets_category ON pets(category_id)");
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pets_created_at ON pets(created_at)");
                
            } catch (Exception e) {
                System.err.println("Failed to initialize test database: " + e.getMessage());
                throw new RuntimeException("Database initialization failed", e);
            }
        }
    }
}