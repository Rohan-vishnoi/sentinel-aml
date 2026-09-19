package com.sentinelaml.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionIngestRequest(
        @NotBlank String transactionId,
        @NotBlank String accountId,
        @NotNull BigDecimal amount,
        @NotBlank String currency,
        String counterparty,
        String channel,
        String jurisdiction,
        @NotNull Instant transactionTimestamp,
        @NotBlank String direction,
        @NotBlank String transactionType
) {
}
