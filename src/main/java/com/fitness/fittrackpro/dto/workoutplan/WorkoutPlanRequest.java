package com.fitness.fittrackpro.dto.workoutplan;

import com.fitness.fittrackpro.model.enums.Difficulty;
import com.fitness.fittrackpro.model.enums.Goal;
import jakarta.validation.constraints.*;

public record WorkoutPlanRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull @Min(1) @Max(52) Integer durationWeeks,
        @NotNull Difficulty difficulty,
        @NotNull Goal goal,
        Boolean published
) {
}
