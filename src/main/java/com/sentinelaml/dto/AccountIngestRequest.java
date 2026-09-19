package com.sentinelaml.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountIngestRequest(
        @NotBlank String accountId,
        @NotBlank String customerId,
        @NotBlank String accountType,
        @NotBlank String accountStatus,
        @NotBlank String currency,
        LocalDate openDate,
        LocalDate closeDate,
        String branchCode,
        String branchCity,
        BigDecimal currentBalance,
        BigDecimal avgMonthlyBalance6m,
        BigDecimal creditLimit,
        BigDecimal creditUtilizationPct,
        @NotNull Boolean overdraftEnabled,
        String cardType,
        @NotNull Boolean jointAccount,
        Integer numLinkedDevices,
        @NotNull Boolean mobileBankingEnrolled,
        LocalDate lastLoginDate,
        Integer avgMonthlyTxnCount,
        String accountTier
) {
}
