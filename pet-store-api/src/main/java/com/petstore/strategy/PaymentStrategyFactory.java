package com.petstore.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.petstore.enums.PaymentType;
import com.petstore.exception.UnsupportedPaymentTypeException;
import com.petstore.strategy.payment.PaymentStrategy;

@Component
public class PaymentStrategyFactory {
    
    private final Map<PaymentType, PaymentStrategy> strategies;
    
    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::getPaymentType,
                        Function.identity()
                ));
    }
    
    public PaymentStrategy getStrategy(PaymentType paymentType) {
        PaymentStrategy strategy = strategies.get(paymentType);
        if (strategy == null) {
            throw new UnsupportedPaymentTypeException(
                    "Payment type not supported: " + paymentType);
        }
        return strategy;
    }
}
