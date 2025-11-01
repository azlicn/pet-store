package com.petstore.dto;

import com.petstore.enums.WalletType;
import com.petstore.enums.PaymentType;

import jakarta.validation.constraints.NotNull;

public class PaymentOrderRequest {

    @NotNull(message = "Shipping address ID must not be null")
    private Long shippingAddressId;

    @NotNull(message = "Billing address ID must not be null")
    private Long billingAddressId;

    @NotNull(message = "Payment type must not be null")
    private PaymentType paymentType;

    private WalletType walletType;

    private String cardNumber;

    private String walletId;

    private String paypalId;

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

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getPaypalId() {
        return paypalId;
    }

    public void setPaypalId(String paypalId) {
        this.paypalId = paypalId;
    }

    public String getPaymentNote() {
        return paymentNote;
    }

    public void setPaymentNote(String paymentNote) {
        this.paymentNote = paymentNote;
    }

    @Override
    public String toString() {
        return "PaymentOrderRequest [shippingAddressId=" + shippingAddressId + ", billingAddressId=" + billingAddressId
                + ", paymentType=" + paymentType + ", eWalletType=" + walletType + ", cardNumber=" + cardNumber
                + ", walletId=" + walletId + ", paypalId=" + paypalId + ", paymentNote=" + paymentNote + "]";
    }

}
