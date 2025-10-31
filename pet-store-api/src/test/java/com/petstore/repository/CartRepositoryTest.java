package com.petstore.repository;

import com.petstore.model.Cart;
import com.petstore.model.User;
import com.petstore.model.Pet;
import com.petstore.model.CartItem;
import com.petstore.model.Category;

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
@DisplayName("Cart Repository Tests")
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

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

        testCategory = new Category();
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
    @DisplayName("Find by user ID - Should return cart when user exists")
    void findByUserId_ShouldReturnCartWhenUserExists() {
        Optional<Cart> found = cartRepository.findByUserId(testUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Find by user ID - Should return empty when user does not exist")
    void findByUserId_ShouldReturnEmptyWhenUserDoesNotExist() {
        Optional<Cart> found = cartRepository.findByUserId(99999L);
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Find by user ID with items and pets - Should return cart with items and pets")
    void findByUserIdWithItemsAndPets_ShouldReturnCartWithItemsAndPets() {
        Optional<Cart> found = cartRepository.findByUserIdWithItemsAndPets(testUser.getId());
        assertThat(found).isPresent();
        Cart cart = found.get();
        assertThat(cart.getItems()).hasSize(1);
        CartItem item = cart.getItems().get(0);
        assertThat(item.getPet().getName()).isEqualTo("Buddy");
        assertThat(item.getPrice().compareTo(BigDecimal.valueOf(100))).isEqualTo(0);
    }
}
