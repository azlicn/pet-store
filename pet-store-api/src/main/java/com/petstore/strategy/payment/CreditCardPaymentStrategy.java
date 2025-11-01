package com.petstore.strategy.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.PaymentType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardPaymentStrategy.class);

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.CREDIT_CARD;
    }

    @Override
    public void processPayment(Payment payment, PaymentOrderRequest request) {
        
        logger.info("Processing credit card payment for amount: {}", payment.getAmount());

        // Add credit card specific details to payment note if needed
        payment.setPaymentNote(request.getCardNumber());
    }

    @Override
    public void validatePayment(PaymentOrderRequest request) {
         // Validate credit card specific fields
        logger.info("Validating credit card payment details");
        if (request.getCardNumber() == null || request.getCardNumber().isEmpty()) {
            throw new InvalidPaymentException("Card number is required for credit card payments.");
        }
    }
    
}
