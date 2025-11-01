package com.petstore.strategy.payment.ewallet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.WalletType;
import com.petstore.model.Payment;

/**
 * Test class for BoostPayStrategy
 */
public class BoostPayStrategyTest {
    
    private BoostPayStrategy strategy;
    private Payment payment;
    private PaymentOrderRequest request;

    @BeforeEach
    void setUp() {
        strategy = new BoostPayStrategy();
        
        payment = new Payment();
        payment.setAmount(new BigDecimal("75.00"));
        
        request = new PaymentOrderRequest();
        request.setWalletType(WalletType.BOOSTPAY);
        request.setWalletId("+60123456789");
    }

    /**
     * Test to verify that the e-wallet type returned is BOOSTPAY
     */
    @Test
    @DisplayName("Should return BOOSTPAY as e-wallet type")
    void testGetEWalletType() {
        assertEquals(WalletType.BOOSTPAY, strategy.getWalletType());
    }

    /**
     * Test to verify that processing e-wallet payment sets the payment note correctly
     */
    @Test
    @DisplayName("Should process BoostPay payment successfully")
    void testProcessEWalletPayment() {
        strategy.processEWalletPayment(payment, request);
        
        assertNotNull(payment.getPaymentNote());
        assertTrue(payment.getPaymentNote().contains("BOOSTPAY"));
    }

    /**
     * Test to validate BoostPay payment request with valid phone number
     */
    @Test
    @DisplayName("Should validate BoostPay request successfully")
    void testValidateEWalletPayment() {
        assertDoesNotThrow(() -> strategy.validateEWalletPayment(request));
    }
}
