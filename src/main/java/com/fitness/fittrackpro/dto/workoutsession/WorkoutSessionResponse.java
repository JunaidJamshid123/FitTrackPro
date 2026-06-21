package com.fitness.fittrackpro.dto.workoutsession;

import com.fitness.fittrackpro.model.WorkoutSession;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkoutSessionResponse(
        Long id,
        LocalDate sessionDate,
        Integer durationMinutes,
        Integer caloriesBurned,
        Long userId,
        Long workoutPlanId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static WorkoutSessionResponse from(WorkoutSession session) {
        return new WorkoutSessionResponse(
                session.getId(),
                session.getSessionDate(),
                session.getDurationMinutes(),
                session.getCaloriesBurned(),
                session.getUser().getId(),
                session.getPlan() != null ? session.getPlan().getId() : null,
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
