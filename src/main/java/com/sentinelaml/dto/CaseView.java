package com.sentinelaml.dto;

import java.time.Instant;

public record CaseView(
        String id,
        String caseNumber,
        String alertId,
        String customerId,
        String status,
        String assignedAnalyst,
        String dispositionReason,
        String dispositionedBy,
        Instant dispositionedAt
) {
}
