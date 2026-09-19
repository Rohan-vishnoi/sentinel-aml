package com.sentinelaml.service;

public record DetectedAlert(
        String alertKey,
        String customerId,
        String accountId,
        String primaryRule,
        String triggeredRulesCsv,
        String evidenceCsv,
        String explanation,
        Integer riskScore,
        String status,
        String dispositionReason,
        String analystId
) {
}
