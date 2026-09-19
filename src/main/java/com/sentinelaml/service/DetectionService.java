package com.sentinelaml.service;

import com.sentinelaml.domain.TransactionRecord;
import com.sentinelaml.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DetectionService {

    private final TransactionRepository transactionRepository;
    private final AlertService alertService;
    private final RuleConfigService ruleConfigService;

    public DetectionService(TransactionRepository transactionRepository, AlertService alertService, RuleConfigService ruleConfigService) {
        this.transactionRepository = transactionRepository;
        this.alertService = alertService;
        this.ruleConfigService = ruleConfigService;
    }

    @Transactional
    public List<String> evaluate(TransactionRecord transaction) {
        List<DetectedAlert> candidates = new java.util.ArrayList<>();
        candidates.addAll(checkCtr(transaction));
        candidates.addAll(checkStructuring(transaction));
        candidates.addAll(checkRapidMovement(transaction));
        candidates.addAll(checkHighRiskJurisdiction(transaction));
        candidates.addAll(checkBehavioralDeviation(transaction));
        candidates.addAll(checkRoundNumber(transaction));
        return candidates.stream().map(candidate -> alertService.upsertAlert(candidate).getId()).toList();
    }

    private List<DetectedAlert> checkCtr(TransactionRecord transaction) {
        BigDecimal threshold = ruleConfigService.getNumeric("CTR_THRESHOLD_BASE", BigDecimal.valueOf(10000));
        if (transaction.getNormalizedAmountBase().compareTo(threshold) < 0) {
            return List.of();
        }
        return List.of(candidate(
                transaction,
                "CTR_THRESHOLD",
                transaction.getTransactionId(),
                "Single transaction exceeded the reporting threshold with normalized value " + transaction.getNormalizedAmountBase(),
                70,
                transaction.getTransactionId()
        ));
    }

    private List<DetectedAlert> checkStructuring(TransactionRecord transaction) {
        if (!ruleConfigService.isEnabled("STRUCTURING_ENABLED", true)) {
            return List.of();
        }
        Instant from = transaction.getTransactionTimestamp().minusSeconds(24 * 3600);
        List<TransactionRecord> recent = transactionRepository.findByAccount_AccountIdAndTransactionTimestampBetween(
                transaction.getAccount().getAccountId(), from, transaction.getTransactionTimestamp());
        List<TransactionRecord> suspicious = recent.stream()
                .filter(tx -> tx.getNormalizedAmountBase().compareTo(BigDecimal.valueOf(9000)) >= 0
                        && tx.getNormalizedAmountBase().compareTo(BigDecimal.valueOf(9999.99)) <= 0)
                .toList();
        if (suspicious.size() < 3) {
            return List.of();
        }
        return List.of(candidate(
                transaction,
                "STRUCTURING",
                transaction.getAccount().getAccountId() + "|" + transaction.getTransactionTimestamp().atZone(ZoneOffset.UTC).toLocalDate(),
                "Three or more transactions just below the reporting threshold were observed within 24 hours.",
                85,
                suspicious.stream().map(TransactionRecord::getTransactionId).collect(Collectors.joining(","))
        ));
    }

    private List<DetectedAlert> checkRapidMovement(TransactionRecord transaction) {
        if (!ruleConfigService.isEnabled("RAPID_MOVEMENT_ENABLED", true)) {
            return List.of();
        }
        Instant windowStart = transaction.getTransactionTimestamp().minusSeconds(48 * 3600);
        List<TransactionRecord> recent = transactionRepository.findByAccount_AccountIdAndTransactionTimestampBetween(
                transaction.getAccount().getAccountId(), windowStart, transaction.getTransactionTimestamp());
        List<TransactionRecord> deposits = recent.stream().filter(tx -> "CREDIT".equalsIgnoreCase(tx.getDirection())).toList();
        List<TransactionRecord> withdrawals = recent.stream().filter(tx -> "DEBIT".equalsIgnoreCase(tx.getDirection())).toList();
        for (TransactionRecord deposit : deposits) {
            BigDecimal outgoing = withdrawals.stream()
                    .filter(tx -> tx.getTransactionTimestamp().isAfter(deposit.getTransactionTimestamp()) || tx.getTransactionTimestamp().equals(deposit.getTransactionTimestamp()))
                    .map(tx -> tx.getNormalizedAmountBase())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal threshold = deposit.getNormalizedAmountBase().multiply(BigDecimal.valueOf(0.8)).setScale(2, RoundingMode.HALF_UP);
            if (outgoing.compareTo(threshold) >= 0) {
                return List.of(candidate(
                        transaction,
                        "RAPID_MOVEMENT",
                        deposit.getTransactionId(),
                        "Funds deposited into the account were rapidly moved out within 48 hours.",
                        90,
                        deposit.getTransactionId() + "," + transaction.getTransactionId()
                ));
            }
        }
        return List.of();
    }

    private List<DetectedAlert> checkHighRiskJurisdiction(TransactionRecord transaction) {
        Set<String> highRisk = ruleConfigService.getTextSet("HIGH_RISK_JURISDICTIONS", Set.of("IR", "KP", "SY", "RU"));
        if (transaction.getJurisdiction() == null || !highRisk.contains(transaction.getJurisdiction())) {
            return List.of();
        }
        return List.of(candidate(
                transaction,
                "HIGH_RISK_JURISDICTION",
                transaction.getTransactionId() + "|" + transaction.getJurisdiction(),
                "Transaction involves a configured high-risk or sanctions jurisdiction.",
                95,
                transaction.getTransactionId()
        ));
    }

    private List<DetectedAlert> checkBehavioralDeviation(TransactionRecord transaction) {
        LocalDate currentDay = transaction.getTransactionTimestamp().atZone(ZoneOffset.UTC).toLocalDate();
        Instant start = currentDay.minusDays(90).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = currentDay.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        List<TransactionRecord> history = transactionRepository.findByAccount_AccountIdAndTransactionTimestampBetween(
                transaction.getAccount().getAccountId(), start, end);
        BigDecimal currentDaySum = history.stream()
                .filter(tx -> tx.getTransactionTimestamp().atZone(ZoneOffset.UTC).toLocalDate().equals(currentDay))
                .map(TransactionRecord::getNormalizedAmountBase)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ninetyDaySum = history.stream()
                .filter(tx -> tx.getTransactionTimestamp().isBefore(end) && tx.getTransactionTimestamp().isAfter(start))
                .map(TransactionRecord::getNormalizedAmountBase)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (history.size() < 2) {
            return List.of();
        }
        BigDecimal avgDaily = ninetyDaySum.divide(BigDecimal.valueOf(90), 2, RoundingMode.HALF_UP);
        if (avgDaily.signum() == 0 || currentDaySum.compareTo(avgDaily.multiply(BigDecimal.valueOf(3))) <= 0) {
            return List.of();
        }
        return List.of(candidate(
                transaction,
                "BEHAVIORAL_DEVIATION",
                transaction.getAccount().getCustomer().getCustomerId() + "|" + currentDay,
                "Daily transaction value exceeded three times the 90-day rolling average.",
                75,
                history.stream().filter(tx -> tx.getTransactionTimestamp().atZone(ZoneOffset.UTC).toLocalDate().equals(currentDay)).map(TransactionRecord::getTransactionId).collect(Collectors.joining(","))
        ));
    }

    private List<DetectedAlert> checkRoundNumber(TransactionRecord transaction) {
        boolean roundAmount = transaction.getNormalizedAmountBase().remainder(BigDecimal.valueOf(1000)).compareTo(BigDecimal.ZERO) == 0
                || (transaction.getNormalizedAmountBase().compareTo(BigDecimal.valueOf(9000)) >= 0
                && transaction.getNormalizedAmountBase().compareTo(BigDecimal.valueOf(9999.99)) <= 0);
        if (!roundAmount) {
            return List.of();
        }
        Instant from = transaction.getTransactionTimestamp().minusSeconds(24 * 3600);
        List<TransactionRecord> recent = transactionRepository.findByAccount_AccountIdAndTransactionTimestampBetween(
                transaction.getAccount().getAccountId(), from, transaction.getTransactionTimestamp());
        List<TransactionRecord> matching = recent.stream()
                .filter(tx -> tx.getNormalizedAmountBase().remainder(BigDecimal.valueOf(1000)).compareTo(BigDecimal.ZERO) == 0
                        || (tx.getNormalizedAmountBase().compareTo(BigDecimal.valueOf(9000)) >= 0
                        && tx.getNormalizedAmountBase().compareTo(BigDecimal.valueOf(9999.99)) <= 0))
                .toList();
        if (matching.size() < 2) {
            return List.of();
        }
        return List.of(candidate(
                transaction,
                "ROUND_NUMBER_PATTERN",
                transaction.getAccount().getAccountId() + "|" + transaction.getTransactionTimestamp().atZone(ZoneOffset.UTC).toLocalDate(),
                "Repeated round or just-below-threshold amounts were observed.",
                40,
                matching.stream().map(TransactionRecord::getTransactionId).collect(Collectors.joining(","))
        ));
    }

    private DetectedAlert candidate(TransactionRecord transaction, String rule, String key, String explanation, int score, String evidence) {
        return new DetectedAlert(
                transaction.getAccount().getCustomer().getCustomerId() + "|" + rule + "|" + key,
                transaction.getAccount().getCustomer().getCustomerId(),
                transaction.getAccount().getAccountId(),
                rule,
                rule,
                evidence,
                explanation,
                score,
                "OPEN",
                null,
                null
        );
    }
}
