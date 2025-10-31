package com.petstore.repository;

import com.petstore.model.CartItem;
import com.petstore.model.Category;
import com.petstore.model.Cart;
import com.petstore.model.Pet;
import com.petstore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CartItem Repository Tests")
class CartItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    private User testUser;
    private Cart testCart;
    private Category testCategory;
    private Pet testPet;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password");
        testUser = entityManager.persistAndFlush(testUser);

        testCart = new Cart();
        testCart.setUser(testUser);
        testCart = entityManager.persistAndFlush(testCart);

        testCategory = new com.petstore.model.Category();
        testCategory.setName("Dog");
        testCategory = entityManager.persistAndFlush(testCategory);

        testPet = new Pet();
        testPet.setName("Buddy");
        testPet.setCategory(testCategory);
        testPet.setPrice(BigDecimal.valueOf(100));
        testPet = entityManager.persistAndFlush(testPet);

        cartItem = new CartItem();
        cartItem.setCart(testCart);
        cartItem.setPet(testPet);
        cartItem.setPrice(BigDecimal.valueOf(100));
        cartItem = entityManager.persistAndFlush(cartItem);

        entityManager.clear();
    }

    @Test
    @DisplayName("Find by ID - Should return cart item when ID exists")
    void findById_ShouldReturnCartItemWhenIdExists() {
    Optional<CartItem> found = cartItemRepository.findById(cartItem.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(cartItem.getId());
    assertThat(found.get().getPrice().compareTo(BigDecimal.valueOf(100))).isEqualTo(0);
    }

    @Test
    @DisplayName("Find by ID - Should return empty when ID does not exist")
    void findById_ShouldReturnEmptyWhenIdDoesNotExist() {
        Optional<CartItem> found = cartItemRepository.findById(99999L);
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Exists by ID - Should return true when ID exists")
    void existsById_ShouldReturnTrueWhenIdExists() {
        boolean exists = cartItemRepository.existsById(cartItem.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Exists by ID - Should return false when ID does not exist")
    void existsById_ShouldReturnFalseWhenIdDoesNotExist() {
        boolean exists = cartItemRepository.existsById(99999L);
        assertThat(exists).isFalse();
    }
}
