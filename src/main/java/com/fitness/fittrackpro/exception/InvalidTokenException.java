package com.fitness.fittrackpro.exception;

/** Thrown when a refresh token is missing, expired, revoked, or tampered with. */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
