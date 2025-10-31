package com.petstore.repository;

import com.petstore.model.Order;
import com.petstore.model.User;
import com.petstore.model.Address;
import com.petstore.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Order Repository Tests")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private User testUser;
    private Address shippingAddress;
    private Address billingAddress;
    private Order order;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password");
        testUser = entityManager.persistAndFlush(testUser);

        shippingAddress = new Address();
        shippingAddress.setUser(testUser);
        shippingAddress.setFullName("Test User");
        shippingAddress.setPhoneNumber("1234567890");
        shippingAddress.setStreet("123 Main St");
        shippingAddress.setCity("Springfield");
        shippingAddress.setState("IL");
        shippingAddress.setPostalCode("11111");
        shippingAddress.setCountry("USA");
        shippingAddress = entityManager.persistAndFlush(shippingAddress);

        billingAddress = new Address();
        billingAddress.setUser(testUser);
        billingAddress.setFullName("Test User");
        billingAddress.setPhoneNumber("1234567890");
        billingAddress.setStreet("456 Elm St");
        billingAddress.setCity("Springfield");
        billingAddress.setState("IL");
        billingAddress.setPostalCode("22222");
        billingAddress.setCountry("USA");
        billingAddress = entityManager.persistAndFlush(billingAddress);

        order = new Order();
        order.setUser(testUser);
        order.setOrderNumber("ORD123");
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.valueOf(200));
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        order = entityManager.persistAndFlush(order);

        entityManager.clear();
    }

    @Test
    @DisplayName("Find by user ID - Should return orders for user")
    void findByUserId_ShouldReturnOrdersForUser() {
        List<Order> orders = orderRepository.findByUserId(testUser.getId());
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getOrderNumber()).isEqualTo("ORD123");
    }

    @Test
    @DisplayName("Find by user ID - Should return empty list for unknown user")
    void findByUserId_ShouldReturnEmptyListForUnknownUser() {
        List<Order> orders = orderRepository.findByUserId(99999L);
        assertThat(orders).isEmpty();
    }

    @Test
    @DisplayName("Exists by address used - Should return true if address used")
    void existsByAddressUsed_ShouldReturnTrueIfAddressUsed() {
        boolean exists = orderRepository.existsByAddressUsed(shippingAddress);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Exists by address used - Should return false if address not used")
    void existsByAddressUsed_ShouldReturnFalseIfAddressNotUsed() {
        Address unusedAddress = new Address();
        unusedAddress.setUser(testUser);
        unusedAddress.setFullName("Unused User");
        unusedAddress.setPhoneNumber("0000000000");
        unusedAddress.setStreet("789 Oak St");
        unusedAddress.setCity("Springfield");
        unusedAddress.setState("IL");
        unusedAddress.setPostalCode("33333");
        unusedAddress.setCountry("USA");
        unusedAddress = entityManager.persistAndFlush(unusedAddress);

        boolean exists = orderRepository.existsByAddressUsed(unusedAddress);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Find by ID and user ID - Should return order when both match")
    void findByIdAndUserId_ShouldReturnOrderWhenBothMatch() {
        Optional<Order> found = orderRepository.findByIdAndUserId(order.getId(), testUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getOrderNumber()).isEqualTo("ORD123");
    }

    @Test
    @DisplayName("Find by ID and user ID - Should return empty when not match")
    void findByIdAndUserId_ShouldReturnEmptyWhenNotMatch() {
        Optional<Order> found = orderRepository.findByIdAndUserId(99999L, testUser.getId());
        assertThat(found).isNotPresent();
    }
}
