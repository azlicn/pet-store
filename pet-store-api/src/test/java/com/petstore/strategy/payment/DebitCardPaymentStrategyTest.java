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
 * Test class for DebitCardPaymentStrategy
 */
public class DebitCardPaymentStrategyTest {

    private DebitCardPaymentStrategy strategy;
    private Payment payment;
    private PaymentOrderRequest request;

    @BeforeEach
    void setUp() {
        strategy = new DebitCardPaymentStrategy();
        
        payment = new Payment();
        payment.setAmount(new BigDecimal("150.00"));
        
        request = new PaymentOrderRequest();
        request.setCardNumber("9876-5432-1098-7654");
        request.setPaymentType(PaymentType.DEBIT_CARD);
    }

    /**
     * Test to verify that the payment type returned is DEBIT_CARD
     */
    @Test
    @DisplayName("Should return DEBIT_CARD as payment type")
    void testGetPaymentType() {
        assertEquals(PaymentType.DEBIT_CARD, strategy.getPaymentType());
    }

    /**
     * Test to verify that processing payment sets the payment note correctly
     */
    @Test
    @DisplayName("Should process debit card payment successfully")
    void testProcessPayment() {
        strategy.processPayment(payment, request);
        
        assertNotNull(payment.getPaymentNote());
        assertEquals("9876-5432-1098-7654", payment.getPaymentNote());
    }

    /**
     * Test to validate payment request with valid card number
     */
    @Test
    @DisplayName("Should validate payment request successfully")
    void testValidatePayment() {
        assertDoesNotThrow(() -> strategy.validatePayment(request));
    }
    
}
