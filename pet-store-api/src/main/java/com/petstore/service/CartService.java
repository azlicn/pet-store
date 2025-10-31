package com.petstore.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

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

/**
 * Service for managing user carts in the store
 */
@Service
public class CartService {

    private final Logger logger = Logger.getLogger(CartService.class.getName());

    private final CartRepository cartRepository;
    private final PetRepository petRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, PetRepository petRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.petRepository = petRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Adds a pet to the user's cart
     *
     * @param userId the user ID
     * @param petId the pet ID to add
     * @return the updated cart
     * @throws PetNotFoundException if the pet does not exist
     * @throws PetAlreadySoldException if the pet is already sold
     * @throws PetAlreadyExistInUserCartException if the pet is already in the cart
     */
    public Cart addPetToCart(Long userId, Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException(petId));

        if (pet.getStatus() == PetStatus.SOLD) {
            throw new PetAlreadySoldException(petId);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(new User(userId));
                    return cartRepository.save(newCart);
                });

        boolean alreadyExists = cart.getItems().stream()
                .anyMatch(item -> item.getPet().getId().equals(petId));

        if (alreadyExists) {
            throw new PetAlreadyExistInUserCartException(petId);
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setPet(pet);
        item.setPrice(pet.getPrice());

        cart.getItems().add(item);

        return cartRepository.save(cart);
    }

    /**
     * Retrieves the cart for a user by user ID
     *
     * @param userId the user ID
     * @return the user's cart
     * @throws UserCartNotFoundException if the cart does not exist
     */
    public Cart getCartByUserId(Long userId) {
        
        return cartRepository.findByUserIdWithItemsAndPets(userId)
                .orElseThrow(() -> new UserCartNotFoundException(userId));
    }

    /**
     * Removes a cart item by its ID
     *
     * @param cartItemId the cart item ID to remove
     * @throws CartItemNotFoundException if the cart item does not exist
     */
    public void removeCartItem(Long cartItemId) {

        if (!cartItemRepository.existsById(cartItemId)) {
            throw new CartItemNotFoundException(cartItemId);
        }
        cartItemRepository.deleteById(cartItemId);
    }

}
