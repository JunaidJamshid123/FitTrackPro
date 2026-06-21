package com.fitness.fittrackpro.exception;

public class ProgressLogNotFoundException extends RuntimeException {
    public ProgressLogNotFoundException(Long id) {
        super("Progress log not found with id: " + id);
    }
}
