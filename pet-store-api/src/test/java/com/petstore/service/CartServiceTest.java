package com.petstore.service;

import com.petstore.enums.PetStatus;
import com.petstore.exception.CartItemNotFoundException;
import com.petstore.exception.PetAlreadyExistInUserCartException;
import com.petstore.exception.PetAlreadySoldException;
import com.petstore.exception.PetNotFoundException;
import com.petstore.exception.UserCartNotFoundException;
import com.petstore.model.Cart;
import com.petstore.model.CartItem;
import com.petstore.model.Pet;
import com.petstore.model.User;
import com.petstore.repository.CartItemRepository;
import com.petstore.repository.CartRepository;
import com.petstore.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CartService} covering cart operations, edge cases, and exception scenarios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Cart Service Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Pet testPet;
    private Cart testCart;
    private CartItem testCartItem;

    /**
     * Initializes test user, pet, cart, and cart item before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User(1L);
        testPet = new Pet();
        testPet.setId(100L);
        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setPrice(BigDecimal.valueOf(99.99));
        testCart = new Cart();
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>());
        testCartItem = new CartItem();
        testCartItem.setId(200L);
        testCartItem.setCart(testCart);
        testCartItem.setPet(testPet);
        testCartItem.setPrice(testPet.getPrice());
    }

    /**
     * Tests adding a pet to the cart successfully.
     */
    @Test
    void addPetToCart_ShouldAddPet() {
        when(petRepository.findById(100L)).thenReturn(Optional.of(testPet));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Cart updated = cartService.addPetToCart(1L, 100L);
        assertThat(updated.getItems()).hasSize(1);
        assertThat(updated.getItems().get(0).getPet()).isEqualTo(testPet);
        verify(petRepository).findById(100L);
        verify(cartRepository).findByUserId(1L);
        verify(cartRepository).save(testCart);
    }

    /**
     * Tests adding a pet to the cart when the pet does not exist (edge case).
     */
    @Test
    void addPetToCart_PetNotFound_ShouldThrowException() {
        when(petRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cartService.addPetToCart(1L, 999L))
            .isInstanceOf(PetNotFoundException.class);
        verify(petRepository).findById(999L);
        verify(cartRepository, never()).findByUserId(any());
    }

    /**
     * Tests adding a pet to the cart when the pet is already sold (edge case).
     */
    @Test
    void addPetToCart_PetAlreadySold_ShouldThrowException() {
        testPet.setStatus(PetStatus.SOLD);
        when(petRepository.findById(100L)).thenReturn(Optional.of(testPet));
        assertThatThrownBy(() -> cartService.addPetToCart(1L, 100L))
            .isInstanceOf(PetAlreadySoldException.class);
        verify(petRepository).findById(100L);
        verify(cartRepository, never()).findByUserId(any());
    }

    /**
     * Tests adding a pet to the cart when the pet is already in the cart (edge case).
     */
    @Test
    void addPetToCart_PetAlreadyInCart_ShouldThrowException() {
        testCart.getItems().add(testCartItem);
        when(petRepository.findById(100L)).thenReturn(Optional.of(testPet));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        assertThatThrownBy(() -> cartService.addPetToCart(1L, 100L))
            .isInstanceOf(PetAlreadyExistInUserCartException.class);
        verify(petRepository).findById(100L);
        verify(cartRepository).findByUserId(1L);
    }

    /**
     * Tests getting a cart by user ID successfully.
     */
    @Test
    void getCartByUserId_ShouldReturnCart() {
        when(cartRepository.findByUserIdWithItemsAndPets(1L)).thenReturn(Optional.of(testCart));
        Cart cart = cartService.getCartByUserId(1L);
        assertThat(cart).isEqualTo(testCart);
        verify(cartRepository).findByUserIdWithItemsAndPets(1L);
    }

    /**
     * Tests getting a cart by user ID when cart does not exist (edge case).
     */
    @Test
    void getCartByUserId_CartNotFound_ShouldThrowException() {
        when(cartRepository.findByUserIdWithItemsAndPets(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cartService.getCartByUserId(2L))
            .isInstanceOf(UserCartNotFoundException.class);
        verify(cartRepository).findByUserIdWithItemsAndPets(2L);
    }

    /**
     * Tests removing a cart item successfully.
     */
    @Test
    void removeCartItem_ShouldRemoveItem() {
        when(cartItemRepository.existsById(200L)).thenReturn(true);
        cartService.removeCartItem(200L);
        verify(cartItemRepository).existsById(200L);
        verify(cartItemRepository).deleteById(200L);
    }

    /**
     * Tests removing a cart item that does not exist (edge case).
     */
    @Test
    void removeCartItem_ItemNotFound_ShouldThrowException() {
        when(cartItemRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> cartService.removeCartItem(999L))
            .isInstanceOf(CartItemNotFoundException.class);
        verify(cartItemRepository).existsById(999L);
        verify(cartItemRepository, never()).deleteById(any(Long.class));
    }
}
