package com.fitness.fittrackpro.dto.exercise;

import com.fitness.fittrackpro.model.Exercise;
import com.fitness.fittrackpro.model.enums.Difficulty;
import com.fitness.fittrackpro.model.enums.MuscleGroup;

public record ExerciseResponse(
        Long id,
        String name,
        String description,
        MuscleGroup muscleGroup,
        Difficulty difficulty,
        String equipment
) {
    public static ExerciseResponse from(Exercise e) {
        return new ExerciseResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getMuscleGroup(),
                e.getDifficulty(),
                e.getEquipment()
        );
    }
}
