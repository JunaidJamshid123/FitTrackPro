package com.fitness.fittrackpro.dto.progresslog;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ProgressLogRequest(
        @NotNull LocalDate logDate,
        @NotNull @DecimalMin("0.1") Double weight,
        @DecimalMin("0.0") @DecimalMax("100.0") Double bodyFatPercent,
        @DecimalMin("0.0") Double chest,
        @DecimalMin("0.0") Double waist,
        @DecimalMin("0.0") Double hips,
        String notes
) {
}
