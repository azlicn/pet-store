
package com.petstore.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

/**
 * Entity class representing an item in a user's shopping cart.
 * Each CartItem links a cart to a specific pet and stores the price at the time
 * of addition.
 */
@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = { "cart_id", "pet_id" }))
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "cart" })
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    @NotNull(message = "Cart is required")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull(message = "Pet is required")
    private Pet pet;

    @Column(nullable = false)
    @NotNull(message = "Price is required")
    private BigDecimal price;

    /**
     * Gets the unique identifier of the cart item.
     * 
     * @return the cart item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the cart item.
     * 
     * @param id the cart item ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the cart to which this item belongs.
     * 
     * @return the cart
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Sets the cart to which this item belongs.
     * 
     * @param cart the cart
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * Gets the pet associated with this cart item.
     * 
     * @return the pet
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * Sets the pet associated with this cart item.
     * 
     * @param pet the pet
     */
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    /**
     * Gets the price of the pet at the time it was added to the cart.
     * 
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price of the pet at the time it was added to the cart.
     * 
     * @param price the price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
