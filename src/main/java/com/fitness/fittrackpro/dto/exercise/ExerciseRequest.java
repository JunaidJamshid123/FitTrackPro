package com.fitness.fittrackpro.dto.exercise;

import com.fitness.fittrackpro.model.enums.Difficulty;
import com.fitness.fittrackpro.model.enums.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExerciseRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull MuscleGroup muscleGroup,
        @NotNull Difficulty difficulty,
        @Size(max = 100) String equipment
) {
}
