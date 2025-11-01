package com.petstore.strategy.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.PaymentType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;
import com.petstore.strategy.EWalletStrategyFactory;
import com.petstore.strategy.payment.ewallet.EWalletStrategy;

@Component
public class EWalletPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(EWalletPaymentStrategy.class);

    @Autowired
    private EWalletStrategyFactory eWalletStrategyFactory;

    @Override
    public PaymentType getPaymentType() {
        return PaymentType.E_WALLET;
    }

    @Override
    public void processPayment(Payment payment, PaymentOrderRequest request) {
        
        logger.info("Processing e-wallet payment for amount: {}", payment.getAmount());

        EWalletStrategy eWalletStrategy = eWalletStrategyFactory.getStrategy(
                request.getWalletType());
        
        eWalletStrategy.processEWalletPayment(payment, request);
    }

    @Override
    public void validatePayment(PaymentOrderRequest request) {

        logger.info("Validating e-wallet payment details for request: {}", request);
        // Validate that e-wallet type is provided
        if (request.getWalletType() == null) {
            throw new InvalidPaymentException("E-Wallet type is required");
        }
        
        // Delegate to specific e-wallet strategy for validation
        EWalletStrategy eWalletStrategy = eWalletStrategyFactory.getStrategy(
                request.getWalletType());
        
        eWalletStrategy.validateEWalletPayment(request);
    }
    
}
