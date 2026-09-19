package com.sentinelaml.service;

import com.sentinelaml.dto.AccountIngestRequest;
import com.sentinelaml.dto.CustomerIngestRequest;
import com.sentinelaml.dto.TransactionIngestRequest;
import com.sentinelaml.domain.ExchangeRate;
import com.sentinelaml.domain.RuleConfig;
import com.sentinelaml.repository.ExchangeRateRepository;
import com.sentinelaml.repository.RuleConfigRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
public class SeedDataService {

    private final IngestionService ingestionService;
    private final ExchangeRateRepository exchangeRateRepository;
    private final RuleConfigRepository ruleConfigRepository;
    private final com.sentinelaml.config.SentinelProperties sentinelProperties;

    public SeedDataService(IngestionService ingestionService, ExchangeRateRepository exchangeRateRepository, RuleConfigRepository ruleConfigRepository, com.sentinelaml.config.SentinelProperties sentinelProperties) {
        this.ingestionService = ingestionService;
        this.exchangeRateRepository = exchangeRateRepository;
        this.ruleConfigRepository = ruleConfigRepository;
        this.sentinelProperties = sentinelProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() throws IOException {
        if (!sentinelProperties.seedEnabled()) {
            return;
        }
        if (ruleConfigRepository.count() == 0) {
            seedRules();
        }
        if (exchangeRateRepository.count() == 0) {
            seedRates();
        }
        if (ingestionService != null) {
            seedCustomers();
            seedAccounts();
            seedTransactions();
        }
    }

    private void seedRules() {
        saveRule("CTR_THRESHOLD_BASE", true, new BigDecimal("10000"), null, "CTR base threshold in INR");
        saveRule("STRUCTURING_ENABLED", true, null, "true", "Enable structuring detection");
        saveRule("RAPID_MOVEMENT_ENABLED", true, null, "true", "Enable rapid movement detection");
        saveRule("HIGH_RISK_JURISDICTIONS", true, null, "IR,KP,SY,RU", "High-risk jurisdictions");
    }

    private void saveRule(String key, boolean enabled, BigDecimal numericValue, String textValue, String description) {
        RuleConfig config = ruleConfigRepository.findByConfigKey(key).orElseGet(RuleConfig::new);
        config.setConfigKey(key);
        config.setEnabled(enabled);
        config.setNumericValue(numericValue);
        config.setTextValue(textValue);
        config.setDescription(description);
        ruleConfigRepository.save(config);
    }

    private void seedRates() {
        saveRate("INR", "INR", BigDecimal.ONE, LocalDate.now());
        saveRate("USD", "INR", new BigDecimal("83.000000"), LocalDate.now());
        saveRate("EUR", "INR", new BigDecimal("90.000000"), LocalDate.now());
    }

    private void saveRate(String from, String to, BigDecimal rate, LocalDate date) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromCurrency(from);
        exchangeRate.setToCurrency(to);
        exchangeRate.setRate(rate);
        exchangeRate.setEffectiveDate(date);
        exchangeRateRepository.save(exchangeRate);
    }

    private void seedCustomers() throws IOException {
        try (BufferedReader reader = reader("data/customers.csv")) {
            reader.lines().skip(1).map(this::customer).forEach(ingestionService::ingestCustomer);
        }
    }

    private void seedAccounts() throws IOException {
        try (BufferedReader reader = reader("data/accounts.csv")) {
            reader.lines().skip(1).map(this::account).forEach(ingestionService::ingestAccount);
        }
    }

    private void seedTransactions() throws IOException {
        try (BufferedReader reader = reader("data/transactions.csv")) {
            reader.lines().skip(1).map(this::transaction).forEach(ingestionService::ingestTransaction);
        }
    }

    private BufferedReader reader(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8));
    }

    private CustomerIngestRequest customer(String line) {
        String[] parts = line.split(",", -1);
        return new CustomerIngestRequest(
                parts[0], parts[1], parts[2], parts[3], LocalDate.parse(parts[4]), Integer.valueOf(parts[5]), parts[6], parts[7],
                parts[8], parts[9], parts[10], parts[11], parts[12], new BigDecimal(parts[13]), parts[14], parts[15], parts[16],
                LocalDate.parse(parts[17]), parts[18], parts[19], parts[20], "1".equals(parts[21]), parts[22], "Y".equalsIgnoreCase(parts[23]),
                "Y".equalsIgnoreCase(parts[24]), Integer.valueOf(parts[25])
        );
    }

    private AccountIngestRequest account(String line) {
        String[] parts = line.split(",", -1);
        return new AccountIngestRequest(
                parts[0], parts[1], parts[2], parts[3], parts[4], LocalDate.parse(parts[5]), parts[6].isBlank() ? null : LocalDate.parse(parts[6]),
                parts[7], parts[8], new BigDecimal(parts[9]), new BigDecimal(parts[10]), new BigDecimal(parts[11]), new BigDecimal(parts[12]),
                "Y".equalsIgnoreCase(parts[13]), parts[14], "1".equals(parts[15]), Integer.valueOf(parts[16]), "Y".equalsIgnoreCase(parts[17]),
                LocalDate.parse(parts[18]), Integer.valueOf(parts[19]), parts[20]
        );
    }

    private TransactionIngestRequest transaction(String line) {
        String[] parts = line.split(",", -1);
        return new TransactionIngestRequest(
                parts[0], parts[1], new BigDecimal(parts[2]), parts[3], parts[4], parts[5], parts[6], Instant.parse(parts[7] + "Z"),
                parts[8], parts[9]
        );
    }
}
