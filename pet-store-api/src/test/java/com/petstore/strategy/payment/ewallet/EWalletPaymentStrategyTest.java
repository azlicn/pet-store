package com.petstore.strategy.payment.ewallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.WalletType;
import com.petstore.enums.PaymentType;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.model.Payment;
import com.petstore.strategy.EWalletStrategyFactory;
import com.petstore.strategy.payment.EWalletPaymentStrategy;

/**
 * Test class for EWalletPaymentStrategy
 */
@ExtendWith(MockitoExtension.class)
public class EWalletPaymentStrategyTest {

    @Mock
    private EWalletStrategyFactory eWalletStrategyFactory;

    @Mock
    private EWalletStrategy mockEWalletStrategy;

    @InjectMocks
    private EWalletPaymentStrategy strategy;

    private Payment payment;
    private PaymentOrderRequest request;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        
        request = new PaymentOrderRequest();
        request.setPaymentType(PaymentType.E_WALLET);
        request.setWalletType(WalletType.GRABPAY);
        request.setWalletId("+60123456789");
        request.setPaymentNote("E-Wallet payment");
    }

    /**
     * Test to verify that the payment type returned is E_WALLET
     */
    @Test
    @DisplayName("Should return E_WALLET as payment type")
    void testGetPaymentType() {
        assertEquals(PaymentType.E_WALLET, strategy.getPaymentType());
    }

    /**
     * Test to verify that the e-wallet strategy is called for processing payments
     */
    @Test
    @DisplayName("Should delegate to correct e-wallet strategy for processing")
    void testProcessPayment() {
        when(eWalletStrategyFactory.getStrategy(WalletType.GRABPAY))
            .thenReturn(mockEWalletStrategy);
        
        strategy.processPayment(payment, request);
        
        verify(eWalletStrategyFactory).getStrategy(WalletType.GRABPAY);
        verify(mockEWalletStrategy).processEWalletPayment(payment, request);
    }

    /**
     * Test to verify that the e-wallet strategy is called for validating payments
     */
    @Test
    @DisplayName("Should delegate to correct e-wallet strategy for validation")
    void testValidatePayment() {
        when(eWalletStrategyFactory.getStrategy(WalletType.GRABPAY))
            .thenReturn(mockEWalletStrategy);
        
        strategy.validatePayment(request);
        
        verify(eWalletStrategyFactory, times(1)).getStrategy(WalletType.GRABPAY);
        verify(mockEWalletStrategy).validateEWalletPayment(request);
    }

    /**
     * Test to verify that the e-wallet strategy is called for validating payments
     */
    @Test
    @DisplayName("Should throw exception when e-wallet type is null")
    void testValidatePayment_NullEWalletType() {
        request.setWalletType(null);
        
        assertThrows(InvalidPaymentException.class,
                () -> strategy.validatePayment(request));
    }
    
}
