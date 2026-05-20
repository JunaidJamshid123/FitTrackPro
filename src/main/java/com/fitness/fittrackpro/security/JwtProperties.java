package com.fitness.fittrackpro.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the {@code app.jwt.*} properties from application.properties.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationMs,
        long refreshTokenExpirationMs,
        String issuer
) {
}
