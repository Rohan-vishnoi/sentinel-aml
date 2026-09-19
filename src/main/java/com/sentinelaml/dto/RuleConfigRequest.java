package com.sentinelaml.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RuleConfigRequest(
        @NotBlank String configKey,
        @NotNull Boolean enabled,
        BigDecimal numericValue,
        String textValue,
        String description
) {
}
