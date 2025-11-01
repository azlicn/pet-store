package com.petstore.strategy.payment.ewallet;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.WalletType;
import com.petstore.model.Payment;

public interface EWalletStrategy {
    
    WalletType getWalletType();
    void processEWalletPayment(Payment payment, PaymentOrderRequest request);
    void validateEWalletPayment(PaymentOrderRequest request);
}
