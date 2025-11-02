package com.petstore.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.petstore.enums.DeliveryStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity class representing a delivery for an order, including recipient details, address, status, and timestamps.
 */
@Entity
@Table(name = "deliveries")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    @NotNull(message = "Order is required for delivery")
    private Order order;

    @NotBlank(message = "Recipient name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Delivery status is required")
    private DeliveryStatus status = DeliveryStatus.PENDING;

    private LocalDateTime createdAt;

    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    /**
     * Gets the unique identifier of the delivery.
     * 
     * @return the delivery ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the delivery.
     * 
     * @param id the delivery ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the order associated with this delivery.
     * 
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the order associated with this delivery.
     * 
     * @param order the order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Gets the recipient's name for the delivery.
     * 
     * @return the recipient name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the recipient's name for the delivery.
     * 
     * @param name the recipient name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the recipient's phone number for the delivery.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the recipient's phone number for the delivery.
     * 
     * @param phone the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the delivery address.
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the delivery address.
     * 
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the delivery status.
     * 
     * @return the delivery status
     */
    public DeliveryStatus getStatus() {
        return status;
    }

    /**
     * Sets the delivery status.
     * 
     * @param status the delivery status
     */
    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the delivery was created.
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the delivery was created.
     * 
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the delivery was shipped.
     * 
     * @return the shipped timestamp
     */
    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    /**
     * Sets the timestamp when the delivery was shipped.
     * 
     * @param shippedAt the shipped timestamp
     */
    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    /**
     * Gets the timestamp when the delivery was completed.
     * 
     * @return the delivered timestamp
     */
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    /**
     * Sets the timestamp when the delivery was completed.
     * 
     * @param deliveredAt the delivered timestamp
     */
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

}
