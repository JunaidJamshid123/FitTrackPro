package com.fitness.fittrackpro.exception;

/** Thrown when a requested exercise id does not exist. */
public class ExerciseNotFoundException extends RuntimeException {
    public ExerciseNotFoundException(Long id) {
        super("Exercise not found: " + id);
    }
}
