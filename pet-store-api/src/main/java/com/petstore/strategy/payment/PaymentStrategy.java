package com.petstore.strategy.payment;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.PaymentType;
import com.petstore.model.Payment;

public interface PaymentStrategy {

    PaymentType getPaymentType();
    void processPayment(Payment payment, PaymentOrderRequest request);
    void validatePayment(PaymentOrderRequest request);
    
}
