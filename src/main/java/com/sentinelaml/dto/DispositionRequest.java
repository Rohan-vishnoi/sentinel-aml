package com.sentinelaml.dto;

import jakarta.validation.constraints.NotBlank;

public record DispositionRequest(
        @NotBlank String status,
        @NotBlank String analystId,
        @NotBlank String reason
) {
}
