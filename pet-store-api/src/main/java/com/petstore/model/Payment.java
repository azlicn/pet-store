package com.petstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.petstore.enums.PaymentStatus;
import com.petstore.enums.PaymentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entity class representing a payment for an order, including amount, status,
 * type, note, and timestamp.
 */
@Entity
@Table(name = "payments")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType = PaymentType.CREDIT_CARD;

    private String paymentNote;

    private LocalDateTime paidAt;

    /**
     * Gets the unique identifier of the payment.
     * 
     * @return the payment ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the payment.
     * 
     * @param id the payment ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the order associated with this payment.
     * 
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the order associated with this payment.
     * 
     * @param order the order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Gets the payment amount.
     * 
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the payment amount.
     * 
     * @param amount the amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the payment status.
     * 
     * @return the payment status
     */
    public PaymentStatus getStatus() {
        return status;
    }

    /**
     * Sets the payment status.
     * 
     * @param status the payment status
     */
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    /**
     * Gets the payment type.
     * 
     * @return the payment type
     */
    public PaymentType getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the payment type.
     * 
     * @param paymentType the payment type
     */
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * Gets the payment note.
     * 
     * @return the payment note
     */
    public String getPaymentNote() {
        return paymentNote;
    }

    /**
     * Sets the payment note.
     * 
     * @param paymentNote the payment note
     */
    public void setPaymentNote(String paymentNote) {
        this.paymentNote = paymentNote;
    }

    /**
     * Gets the timestamp when the payment was made.
     * 
     * @return the paid at timestamp
     */
    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    /**
     * Sets the timestamp when the payment was made.
     * 
     * @param paidAt the paid at timestamp
     */
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

}
