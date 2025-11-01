package com.petstore.strategy.payment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.PaymentType;
import com.petstore.model.Payment;

/**
 * Test class for PayPalPaymentStrategy
 */
public class PayPalPaymentStrategyTest {

    private PayPalPaymentStrategy strategy;
    private Payment payment;
    private PaymentOrderRequest request;

    @BeforeEach
    void setUp() {
        strategy = new PayPalPaymentStrategy();
        
        payment = new Payment();
        payment.setAmount(new BigDecimal("200.00"));
        
        request = new PaymentOrderRequest();
        request.setPaymentType(PaymentType.PAYPAL);
        request.setPaypalId("user@example.com");
    }

    /**
     * Test to verify that the payment type returned is PAYPAL
     */
    @Test
    @DisplayName("Should return PAYPAL as payment type")
    void testGetPaymentType() {
        assertEquals(PaymentType.PAYPAL, strategy.getPaymentType());
    }

    /**
     * Test to verify that processing payment sets the payment note correctly
     */
    @Test
    @DisplayName("Should process PayPal payment successfully")
    void testProcessPayment() {
        strategy.processPayment(payment, request);
        
        assertNotNull(payment.getPaymentNote());
        assertEquals("PayPal ID: user@example.com", payment.getPaymentNote());
    }

    /**
     * Test to validate payment request with valid PayPal ID
     */
    @Test
    @DisplayName("Should validate payment request successfully")
    void testValidatePayment() {
        assertDoesNotThrow(() -> strategy.validatePayment(request));
    }
    
}
