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

import com.petstore.enums.WalletType;
import com.petstore.exception.UnsupportedPaymentException;
import com.petstore.strategy.payment.ewallet.BoostPayStrategy;
import com.petstore.strategy.payment.ewallet.EWalletStrategy;
import com.petstore.strategy.payment.ewallet.GrabPayStrategy;
import com.petstore.strategy.payment.ewallet.TouchNGoStrategy;

/**
 * Test class for EWalletStrategyFactory
 */
public class EWalletStrategyFactoryTest {

    private EWalletStrategyFactory factory;
    private List<EWalletStrategy> strategies;

    @BeforeEach
    void setUp() {
        strategies = Arrays.asList(
            new GrabPayStrategy(),
            new BoostPayStrategy(),
            new TouchNGoStrategy()
        );
        
        factory = new EWalletStrategyFactory(strategies);
    }

    /**
     * Test to verify that the correct strategy is returned for GRABPAY type
     */
    @Test
    @DisplayName("Should return GrabPayStrategy for GRABPAY type")
    void testGetStrategy_GrabPay() {
        EWalletStrategy strategy = factory.getStrategy(WalletType.GRABPAY);
        
        assertNotNull(strategy);
        assertInstanceOf(GrabPayStrategy.class, strategy);
        assertEquals(WalletType.GRABPAY, strategy.getWalletType());
    }

    /**
     * Test to verify that the correct strategy is returned for BOOSTPAY type
     */
    @Test
    @DisplayName("Should return BoostPayStrategy for BOOSTPAY type")
    void testGetStrategy_BoostPay() {
        EWalletStrategy strategy = factory.getStrategy(WalletType.BOOSTPAY);
        
        assertNotNull(strategy);
        assertInstanceOf(BoostPayStrategy.class, strategy);
        assertEquals(WalletType.BOOSTPAY, strategy.getWalletType());
    }

    /**
     * Test to verify that the correct strategy is returned for TOUCHNGO type
     */
    @Test
    @DisplayName("Should return TouchNGoStrategy for TOUCHNGO type")
    void testGetStrategy_TouchNGo() {
        EWalletStrategy strategy = factory.getStrategy(WalletType.TOUCHNGO);
        
        assertNotNull(strategy);
        assertInstanceOf(TouchNGoStrategy.class, strategy);
        assertEquals(WalletType.TOUCHNGO, strategy.getWalletType());
    }

    /**
     * Test to verify that an exception is thrown for unsupported e-wallet types
     */
    @Test
    @DisplayName("Should throw exception for unsupported e-wallet type")
    void testGetStrategy_UnsupportedType() {
        // Test with null
        assertThrows(UnsupportedPaymentException.class, () -> factory.getStrategy(null));
    }

    /**
     * Test to verify that the factory initializes correctly with all strategies
     */
    @Test
    @DisplayName("Should initialize with all e-wallet strategies")
    void testFactoryInitialization() {
        assertNotNull(factory);
        
        assertDoesNotThrow(() -> {
            factory.getStrategy(WalletType.GRABPAY);
            factory.getStrategy(WalletType.BOOSTPAY);
            factory.getStrategy(WalletType.TOUCHNGO);
        });
    }
    
}
