package com.petstore.repository;

import com.petstore.model.Discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Discount Repository Tests")
class DiscountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DiscountRepository discountRepository;

    private Discount discount;

    @BeforeEach
    void setUp() {
        discount = new Discount();
        discount.setCode("SAVE10");
        discount.setPercentage(BigDecimal.valueOf(10));
        discount.setActive(true);
        discount.setValidFrom(LocalDateTime.of(2025, 1, 1, 0, 0));
        discount.setValidTo(LocalDateTime.of(2025, 12, 31, 23, 59));
        discount = entityManager.persistAndFlush(discount);
        entityManager.clear();
    }

    @Test
    @DisplayName("Find by code - Should return discount when code exists")
    void findByCode_ShouldReturnDiscountWhenCodeExists() {
        Optional<Discount> found = discountRepository.findByCode("SAVE10");
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("SAVE10");
        assertThat(found.get().getPercentage().compareTo(BigDecimal.valueOf(10))).isEqualTo(0);
        assertThat(found.get().isActive()).isTrue();
    }

    @Test
    @DisplayName("Find by code - Should return empty when code does not exist")
    void findByCode_ShouldReturnEmptyWhenCodeDoesNotExist() {
        Optional<Discount> found = discountRepository.findByCode("NOTEXIST");
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Exists by code - Should return true when code exists")
    void existsByCode_ShouldReturnTrueWhenCodeExists() {
        boolean exists = discountRepository.existsByCode("SAVE10");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Exists by code - Should return false when code does not exist")
    void existsByCode_ShouldReturnFalseWhenCodeDoesNotExist() {
        boolean exists = discountRepository.existsByCode("NOTEXIST");
        assertThat(exists).isFalse();
    }
}
