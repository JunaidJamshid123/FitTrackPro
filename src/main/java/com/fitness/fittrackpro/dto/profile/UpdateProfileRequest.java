package com.fitness.fittrackpro.dto.profile;

import com.fitness.fittrackpro.model.enums.ActivityLevel;
import jakarta.validation.constraints.*;

/**
 * Mutable fields on the user's extended profile. {@code activityLevel} is required
 * because it drives the recommended-calories calculation.
 */
public record UpdateProfileRequest(
        @NotNull ActivityLevel activityLevel,
        @DecimalMin("20.0") @DecimalMax("400.0") Double targetWeight,
        @Min(800) @Max(8000) Integer targetCalories,
        @Size(max = 500) String bio
) {
}
