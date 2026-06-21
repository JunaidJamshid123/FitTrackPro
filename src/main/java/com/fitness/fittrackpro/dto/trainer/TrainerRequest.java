package com.fitness.fittrackpro.dto.trainer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrainerRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 150) String email,
        @NotBlank @Size(max = 100) String specialization,
        @NotNull @Min(0) Integer experienceYears
) {
}
