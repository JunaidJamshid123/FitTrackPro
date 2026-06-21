package com.fitness.fittrackpro.dto.workoutsession;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record WorkoutSessionRequest(
        @NotNull LocalDate sessionDate,
        @NotNull @Min(1) Integer durationMinutes,
        @Min(0) Integer caloriesBurned,
        Long workoutPlanId
) {
}
