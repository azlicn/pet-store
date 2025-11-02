package com.petstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.petstore.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity class representing a customer order, including items, payment,
 * delivery, addresses, and discount details.
 */
@Entity
@Table(name = "orders")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Order number is required")
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // @JsonIgnore
    @NotNull(message = "User is required")
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Order status is required")
    private OrderStatus status = OrderStatus.PLACED;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;

    // Snapshot of discount values at time of order creation (immutable historical
    // record)
    @Column(name = "discount_code")
    private String discountCode;

    @Column(name = "discount_percentage", precision = 10, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    @JsonIgnore
    private Address shippingAddress;

    // Optional: separate billing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    // @JsonIgnore
    private Address billingAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Sets creation and update timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the last modified timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the unique identifier of the order.
     * 
     * @return the order ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the order.
     * 
     * @param id the order ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the order number.
     * 
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     * 
     * @param orderNumber the order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the user who placed the order.
     * 
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who placed the order.
     * 
     * @param user the user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the status of the order.
     * 
     * @return the order status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the order.
     * 
     * @param status the order status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Gets the total amount for the order.
     * 
     * @return the total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount for the order.
     * 
     * @param totalAmount the total amount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the discount applied to the order.
     * 
     * @return the discount
     */
    public Discount getDiscount() {
        return discount;
    }

    /**
     * Sets the discount applied to the order.
     * 
     * @param discount the discount
     */
    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    /**
     * Gets the discount code snapshot at order creation.
     * 
     * @return the discount code
     */
    public String getDiscountCode() {
        return discountCode;
    }

    /**
     * Sets the discount code snapshot at order creation.
     * 
     * @param discountCode the discount code
     */
    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    /**
     * Gets the discount percentage snapshot at order creation.
     * 
     * @return the discount percentage
     */
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Sets the discount percentage snapshot at order creation.
     * 
     * @param discountPercentage the discount percentage
     */
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    /**
     * Gets the discount amount snapshot at order creation.
     * 
     * @return the discount amount
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the discount amount snapshot at order creation.
     * 
     * @param discountAmount the discount amount
     */
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * Gets the list of items in the order.
     * 
     * @return the list of order items
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Sets the list of items in the order.
     * 
     * @param items the list of order items
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * Gets the payment associated with the order.
     * 
     * @return the payment
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Sets the payment associated with the order.
     * 
     * @param payment the payment
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * Gets the delivery associated with the order.
     * 
     * @return the delivery
     */
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * Sets the delivery associated with the order.
     * 
     * @param delivery the delivery
     */
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    /**
     * Gets the shipping address for the order.
     * 
     * @return the shipping address
     */
    public Address getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the shipping address for the order.
     * 
     * @param shippingAddress the shipping address
     */
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * Gets the billing address for the order.
     * 
     * @return the billing address
     */
    public Address getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the billing address for the order.
     * 
     * @param billingAddress the billing address
     */
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * Gets the creation timestamp of the order.
     * 
     * @return the created at timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the order.
     * 
     * @param createdAt the created at timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last updated timestamp of the order.
     * 
     * @return the updated at timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last updated timestamp of the order.
     * 
     * @param updatedAt the updated at timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
