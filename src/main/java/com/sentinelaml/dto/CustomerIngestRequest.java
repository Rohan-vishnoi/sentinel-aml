package com.sentinelaml.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerIngestRequest(
        @NotBlank String customerId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String gender,
        LocalDate dateOfBirth,
        Integer age,
        @Email String email,
        String phoneNumber,
        String city,
        String state,
        String country,
        String postalCode,
        String occupation,
        BigDecimal annualIncome,
        String maritalStatus,
        String educationLevel,
        String employmentStatus,
        LocalDate customerSince,
        String customerSegment,
        @NotBlank String kycStatus,
        @NotBlank String riskRating,
        @NotNull Boolean politicallyExposed,
        String preferredChannel,
        @NotNull Boolean emailVerified,
        @NotNull Boolean phoneVerified,
        Integer numComplaintsLastYear
) {
}
