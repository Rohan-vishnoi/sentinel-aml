package com.sentinelaml.dto;

import java.time.Instant;

public record AlertView(
        String id,
        String alertKey,
        String customerId,
        String customerNameMasked,
        String accountId,
        String primaryRule,
        String triggeredRulesCsv,
        String evidenceCsv,
        String explanation,
        Integer riskScore,
        String status,
        String dispositionReason,
        String analystId,
        String caseId,
        Instant createdAt
) {
}
