package com.sentinelaml.service;

import com.sentinelaml.config.SentinelProperties;
import com.sentinelaml.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class CurrencyConversionService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final SentinelProperties sentinelProperties;

    public CurrencyConversionService(ExchangeRateRepository exchangeRateRepository, SentinelProperties sentinelProperties) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.sentinelProperties = sentinelProperties;
    }

    public BigDecimal normalizeToBase(BigDecimal amount, String currency, LocalDate effectiveDate) {
        String baseCurrency = sentinelProperties.baseCurrency();
        if (currency.equalsIgnoreCase(baseCurrency)) {
            return amount;
        }
        return exchangeRateRepository
                .findTopByFromCurrencyAndToCurrencyAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(currency, baseCurrency, effectiveDate)
                .map(rate -> amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_UP))
                .orElseThrow(() -> new IllegalStateException("Missing exchange rate for " + currency + "->" + baseCurrency));
    }
}
