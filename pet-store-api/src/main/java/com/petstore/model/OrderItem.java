package com.petstore.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity class representing an item in a customer order, linking a pet to an
 * order and storing the price at purchase time.
 */
@Entity
@Table(name = "order_items")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    private BigDecimal price;

    /**
     * Gets the unique identifier of the order item.
     * 
     * @return the order item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the order item.
     * 
     * @param id the order item ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the order to which this item belongs.
     * 
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the order to which this item belongs.
     * 
     * @param order the order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Gets the pet associated with this order item.
     * 
     * @return the pet
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * Sets the pet associated with this order item.
     * 
     * @param pet the pet
     */
    public void setPet(Pet pet) {
        this.pet = pet;
    }

    /**
     * Gets the price of the pet at the time of purchase.
     * 
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price of the pet at the time of purchase.
     * 
     * @param price the price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
