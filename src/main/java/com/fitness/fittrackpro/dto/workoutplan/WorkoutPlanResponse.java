package com.fitness.fittrackpro.dto.workoutplan;

import com.fitness.fittrackpro.model.WorkoutPlan;
import com.fitness.fittrackpro.model.enums.Difficulty;
import com.fitness.fittrackpro.model.enums.Goal;

import java.time.LocalDateTime;

public record WorkoutPlanResponse(
        Long id,
        String name,
        String description,
        Integer durationWeeks,
        Difficulty difficulty,
        Goal goal,
        Boolean published,
        Long trainerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static WorkoutPlanResponse from(WorkoutPlan plan) {
        return new WorkoutPlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getDurationWeeks(),
                plan.getDifficulty(),
                plan.getGoal(),
                plan.getPublished(),
                plan.getTrainer().getId(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}
