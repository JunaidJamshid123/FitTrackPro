package com.fitness.fittrackpro.exception;

/** Thrown when a requested user id does not exist. */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
