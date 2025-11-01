package com.petstore.strategy.payment.ewallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.WalletType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;

@Component
public class GrabPayStrategy implements EWalletStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GrabPayStrategy.class);
    
    @Override
    public WalletType getWalletType() {
        return WalletType.GRABPAY;
    }

    @Override
    public void processEWalletPayment(Payment payment, PaymentOrderRequest request) {
        
        logger.info("Processing GrabPay payment: {}", payment.getAmount());
        payment.setPaymentNote(WalletType.GRABPAY.name() + " - " + request.getWalletId());
    }

    @Override
    public void validateEWalletPayment(PaymentOrderRequest request) {
        
        if (request.getWalletId() == null) {
            throw new InvalidPaymentException("Wallet Id is required for E-Wallet payments.");
        }
    }
    
}
