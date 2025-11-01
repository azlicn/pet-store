package com.petstore.strategy.payment.ewallet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.WalletType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;

/**
 * Test class for GrabPayStrategy
 */
public class GrabPayStrategyTest {

    private GrabPayStrategy strategy;
    private Payment payment;
    private PaymentOrderRequest request;

    @BeforeEach
    void setUp() {
        strategy = new GrabPayStrategy();
        
        payment = new Payment();
        payment.setAmount(new BigDecimal("50.00"));
        
        request = new PaymentOrderRequest();
        request.setWalletType(WalletType.GRABPAY);
        request.setWalletId("+60123456789");
    }

    /**
     * Test to verify that the e-wallet type returned is GRABPAY
     */
    @Test
    @DisplayName("Should return GRABPAY as e-wallet type")
    void testGetEWalletType() {
        assertEquals(WalletType.GRABPAY, strategy.getWalletType());
    }

    /**
     * Test to verify that processing e-wallet payment sets the payment note correctly
     */
    @Test
    @DisplayName("Should process GrabPay payment successfully")
    void testProcessEWalletPayment() {
        strategy.processEWalletPayment(payment, request);
        
        assertNotNull(payment.getPaymentNote());
        assertTrue(payment.getPaymentNote().contains("GRABPAY"));
    }

    /**
     * Test to validate GrabPay payment request with valid phone number
     */
    @Test
    @DisplayName("Should validate GrabPay request successfully")
    void testValidateEWalletPayment() {
        assertDoesNotThrow(() -> strategy.validateEWalletPayment(request));
    }

    /**
     * Test to validate GrabPay payment request with missing phone number
     */
    @Test
    @DisplayName("Should throw exception when phone number is missing")
    void testValidateEWalletPayment_MissingPhoneNumber() {
        request.setWalletId(null);

        assertThrows(InvalidPaymentException.class,
                () -> strategy.validateEWalletPayment(request));
    }
}
