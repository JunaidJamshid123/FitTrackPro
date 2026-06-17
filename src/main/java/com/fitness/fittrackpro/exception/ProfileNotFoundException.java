package com.fitness.fittrackpro.exception;

/** Thrown when a user has not yet created their extended profile. */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(Long userId) {
        super("Profile not found for user " + userId + ". Create it via PUT /api/users/me/profile.");
    }
}
