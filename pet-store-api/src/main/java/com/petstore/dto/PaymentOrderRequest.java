package com.petstore.dto;

import com.petstore.enums.PaymentType;

import jakarta.validation.constraints.NotNull;

public class PaymentOrderRequest {

    @NotNull(message = "Shipping address ID must not be null")
    private Long shippingAddressId;

    @NotNull(message = "Billing address ID must not be null")
    private Long billingAddressId;

    @NotNull(message = "Payment type must not be null")
    private PaymentType paymentType;
    private String paymentNote;

    public Long getShippingAddressId() {
        return shippingAddressId;
    }
    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }
    public Long getBillingAddressId() {
        return billingAddressId;
    }
    public void setBillingAddressId(Long billingAddressId) {
        this.billingAddressId = billingAddressId;
    }
    public PaymentType getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    public String getPaymentNote() {
        return paymentNote;
    }
    public void setPaymentNote(String paymentNote) {
        this.paymentNote = paymentNote;
    }

    
    
    
}
