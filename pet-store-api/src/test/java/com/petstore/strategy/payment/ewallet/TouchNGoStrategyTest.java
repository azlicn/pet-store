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
 * Test class for TouchNGoStrategy
 */
public class TouchNGoStrategyTest {
    
    private TouchNGoStrategy strategy;
    private Payment payment;
    private PaymentOrderRequest request;

    @BeforeEach
    void setUp() {
        strategy = new TouchNGoStrategy();
        
        payment = new Payment();
        payment.setAmount(new BigDecimal("90.00"));
        
        request = new PaymentOrderRequest();
        request.setWalletType(WalletType.TOUCHNGO);
        request.setWalletId("+60123456789");
    }

    /**
     * Test to verify that the e-wallet type returned is TOUCHNGO
     */
    @Test
    @DisplayName("Should return TOUCHNGO as e-wallet type")
    void testGetEWalletType() {
        assertEquals(WalletType.TOUCHNGO, strategy.getWalletType());
    }

    /**
    * Test to verify that processing e-wallet payment sets the payment note correctly
    */
    @Test
    @DisplayName("Should process Touch 'n Go payment successfully")
    void testProcessEWalletPayment() {
        strategy.processEWalletPayment(payment, request);
        
        assertNotNull(payment.getPaymentNote());
        assertTrue(payment.getPaymentNote().contains("TOUCHNGO"));
    }

    /**
     * Test to validate Touch 'n Go payment request with valid phone number
     */
    @Test
    @DisplayName("Should validate Touch 'n Go request successfully")
    void testValidateEWalletPayment() {
        assertDoesNotThrow(() -> strategy.validateEWalletPayment(request));
    }
}
