package com.petstore.strategy.payment.ewallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.WalletType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;

@Component
public class BoostPayStrategy implements EWalletStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BoostPayStrategy.class);

    @Override
    public WalletType getWalletType() {
        return WalletType.BOOSTPAY;
    }

    @Override
    public void processEWalletPayment(Payment payment, PaymentOrderRequest request) {
        
        logger.info("Processing BoostPay payment: {}", payment.getAmount());
        payment.setPaymentNote(WalletType.BOOSTPAY.name() + " - " + request.getWalletId());
    }

    @Override
    public void validateEWalletPayment(PaymentOrderRequest request) {
        
        if (request.getWalletId() == null) {
            throw new InvalidPaymentException("Wallet Id is required for E-Wallet payments.");
        }
    }
    
}
