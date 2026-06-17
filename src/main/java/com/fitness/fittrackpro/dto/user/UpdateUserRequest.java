package com.fitness.fittrackpro.dto.user;

import com.fitness.fittrackpro.model.enums.Gender;
import com.fitness.fittrackpro.model.enums.Goal;
import jakarta.validation.constraints.*;

/**
 * Mutable profile fields a user may update on themselves.
 * Email, password, and role are intentionally excluded — they have dedicated flows.
 */
public record UpdateUserRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @Min(10) @Max(120) Integer age,
        @NotNull Gender gender,
        @NotNull @DecimalMin("50.0") @DecimalMax("260.0") Double height,
        @NotNull @DecimalMin("20.0") @DecimalMax("400.0") Double weight,
        @NotNull Goal goal
) {
}
