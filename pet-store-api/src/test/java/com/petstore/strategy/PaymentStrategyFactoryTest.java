package com.petstore.strategy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.petstore.enums.PaymentType;
import com.petstore.exception.UnsupportedPaymentTypeException;
import com.petstore.strategy.payment.CreditCardPaymentStrategy;
import com.petstore.strategy.payment.DebitCardPaymentStrategy;
import com.petstore.strategy.payment.EWalletPaymentStrategy;
import com.petstore.strategy.payment.PayPalPaymentStrategy;
import com.petstore.strategy.payment.PaymentStrategy;

/**
 * Test class for PaymentStrategyFactory
 */
public class PaymentStrategyFactoryTest {
    
    private PaymentStrategyFactory factory;
    private List<PaymentStrategy> strategies;

    @BeforeEach
    void setUp() {
        strategies = Arrays.asList(
            new CreditCardPaymentStrategy(),
            new DebitCardPaymentStrategy(),
            new EWalletPaymentStrategy(),
            new PayPalPaymentStrategy()
        );
        
        factory = new PaymentStrategyFactory(strategies);
    }

    /**
     * Test to verify that the correct strategy is returned for CREDIT_CARD type
     */
    @Test
    @DisplayName("Should return CreditCardPaymentStrategy for CREDIT_CARD type")
    void testGetStrategy_CreditCard() {
        PaymentStrategy strategy = factory.getStrategy(PaymentType.CREDIT_CARD);
        
        assertNotNull(strategy);
        assertInstanceOf(CreditCardPaymentStrategy.class, strategy);
        assertEquals(PaymentType.CREDIT_CARD, strategy.getPaymentType());
    }

    /**
    * Test to verify that the correct strategy is returned for DEBIT_CARD type
    */
    @Test
    @DisplayName("Should return DebitCardPaymentStrategy for DEBIT_CARD type")
    void testGetStrategy_DebitCard() {
        PaymentStrategy strategy = factory.getStrategy(PaymentType.DEBIT_CARD);
        
        assertNotNull(strategy);
        assertInstanceOf(DebitCardPaymentStrategy.class, strategy);
        assertEquals(PaymentType.DEBIT_CARD, strategy.getPaymentType());
    }

    /**
     * Test to verify that the correct strategy is returned for E_WALLET type
     */
    @Test
    @DisplayName("Should return EWalletPaymentStrategy for E_WALLET type")
    void testGetStrategy_EWallet() {
        PaymentStrategy strategy = factory.getStrategy(PaymentType.E_WALLET);
        
        assertNotNull(strategy);
        assertInstanceOf(EWalletPaymentStrategy.class, strategy);
        assertEquals(PaymentType.E_WALLET, strategy.getPaymentType());
    }

    /**
     * Test to verify that the correct strategy is returned for PAYPAL type
     */
    @Test
    @DisplayName("Should return PayPalPaymentStrategy for PAYPAL type")
    void testGetStrategy_PayPal() {
        PaymentStrategy strategy = factory.getStrategy(PaymentType.PAYPAL);
        
        assertNotNull(strategy);
        assertInstanceOf(PayPalPaymentStrategy.class, strategy);
        assertEquals(PaymentType.PAYPAL, strategy.getPaymentType());
    }

    /**
     * Test to verify that an exception is thrown for unsupported payment types
     */
    @Test
    @DisplayName("Should throw exception for unsupported payment type")
    void testGetStrategy_UnsupportedType() {

        assertThrows(UnsupportedPaymentTypeException.class, () -> factory.getStrategy(null));
    }

    /**
     * Test to verify that the factory initializes correctly with all strategies
     */
    @Test
    @DisplayName("Should initialize with all payment strategies")
    void testFactoryInitialization() {
        assertNotNull(factory);
        
        assertDoesNotThrow(() -> {
            factory.getStrategy(PaymentType.CREDIT_CARD);
            factory.getStrategy(PaymentType.DEBIT_CARD);
            factory.getStrategy(PaymentType.E_WALLET);
            factory.getStrategy(PaymentType.PAYPAL);
        });
    }
}
