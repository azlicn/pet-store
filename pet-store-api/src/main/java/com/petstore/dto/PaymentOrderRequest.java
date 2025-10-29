package com.petstore.dto;

import com.petstore.enums.PaymentType;

public class PaymentOrderRequest {

    private Long shippingAddressId;
    private Long billingAddressId;
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
