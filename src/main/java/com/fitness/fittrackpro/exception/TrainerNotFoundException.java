package com.fitness.fittrackpro.exception;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(Long id) {
        super("Trainer not found with id: " + id);
    }
}
