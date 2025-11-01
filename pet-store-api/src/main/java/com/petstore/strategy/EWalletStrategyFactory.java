package com.petstore.strategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.petstore.enums.WalletType;
import com.petstore.exception.UnsupportedPaymentException;
import com.petstore.strategy.payment.ewallet.EWalletStrategy;

@Component
public class EWalletStrategyFactory {
    
    private final Map<WalletType, EWalletStrategy> strategies;
    
    public EWalletStrategyFactory(List<EWalletStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        EWalletStrategy::getWalletType,
                        Function.identity()
                ));
    }
    
    public EWalletStrategy getStrategy(WalletType walletType) {
        EWalletStrategy strategy = strategies.get(walletType);
        if (strategy == null) {
            throw new UnsupportedPaymentException(
                    "E-Wallet type not supported: " + walletType);
        }
        return strategy;
    }

}
