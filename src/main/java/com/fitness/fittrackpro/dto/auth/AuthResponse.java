package com.fitness.fittrackpro.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserResponse user
) {
    public static AuthResponse of(String access, String refresh, long expiresInSeconds, UserResponse user) {
        return new AuthResponse(access, refresh, "Bearer", expiresInSeconds, user);
    }
}
