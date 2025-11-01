package com.petstore.strategy.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.PaymentType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;

@Component
public class PayPalPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PayPalPaymentStrategy.class);

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.PAYPAL;
    }

    @Override
    public void processPayment(Payment payment, PaymentOrderRequest request) {

        logger.info("Processing PayPal payment for amount: {}", payment.getAmount());

        // Add PayPal specific details to payment note if needed
        payment.setPaymentNote("PayPal ID: " + request.getPaypalId());
    }

    @Override
    public void validatePayment(PaymentOrderRequest request) {
         // Validate PayPal specific fields
        logger.info("Validating PayPal payment details");

        if (request.getPaypalId() == null) {
            throw new InvalidPaymentException("PayPal ID is required");
        }
    }
    
}
