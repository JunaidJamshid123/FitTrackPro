package com.fitness.fittrackpro.exception;

/** Thrown when registering with an email that already exists. */
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("Email already in use: " + email);
    }
}
