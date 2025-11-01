package com.petstore.strategy.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.PaymentType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;

@Component
public class DebitCardPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DebitCardPaymentStrategy.class);

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.DEBIT_CARD;
    }

    @Override
    public void processPayment(Payment payment, PaymentOrderRequest request) {

        logger.info("Processing debit card payment for amount: {}", payment.getAmount());
        payment.setPaymentNote(request.getCardNumber());
    }

    @Override
    public void validatePayment(PaymentOrderRequest request) {
        
        logger.info("Validating debit card payment details");
        if (request.getCardNumber() == null || request.getCardNumber().isEmpty()) {
            throw new InvalidPaymentException("Card number is required for debit card payments.");
        }
    }

}
