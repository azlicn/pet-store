package com.petstore.strategy.payment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.PaymentType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;

/**
 * Test class for CreditCardPaymentStrategy
 */
public class CreditCardPaymentStrategyTest {

    private CreditCardPaymentStrategy strategy;
    private Payment payment;
    private PaymentOrderRequest request;

    @BeforeEach
    void setUp() {
        strategy = new CreditCardPaymentStrategy();
        
        payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        
        request = new PaymentOrderRequest();
        request.setPaymentType(PaymentType.CREDIT_CARD);
        request.setCardNumber("4111111111111111");

    }

    /**
     * Test to verify that the payment type returned is CREDIT_CARD
     */
    @Test
    @DisplayName("Should return CREDIT_CARD as payment type")
    void testGetPaymentType() {
        assertEquals(PaymentType.CREDIT_CARD, strategy.getPaymentType());
    }

    /**
     * Test to verify that processing payment sets the payment note correctly
     */
    @Test
    @DisplayName("Should process credit card payment successfully")
    void testProcessPayment() {
        strategy.processPayment(payment, request);
        
        assertNotNull(payment.getPaymentNote());
        assertEquals("4111111111111111", payment.getPaymentNote());
    }

    /**
    * Test to validate payment request with valid card number
    */  
    @Test
    @DisplayName("Should validate payment request successfully")
    void testValidatePayment() {
        assertDoesNotThrow(() -> strategy.validatePayment(request));
    }

    /**
     * Test to validate payment request with missing card number
     */
    @Test
    @DisplayName("Should throw exception when card number is missing")
    void testValidatePayment_MissingCardNumber() {
        request.setCardNumber(null);
        
        assertThrows(InvalidPaymentException.class, 
        () -> strategy.validatePayment(request));
    }
    
}
