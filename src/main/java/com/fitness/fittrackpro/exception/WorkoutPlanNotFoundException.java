package com.fitness.fittrackpro.exception;

public class WorkoutPlanNotFoundException extends RuntimeException {
    public WorkoutPlanNotFoundException(Long id) {
        super("Workout plan not found with id: " + id);
    }
}
