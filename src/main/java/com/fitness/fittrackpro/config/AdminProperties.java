package com.fitness.fittrackpro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds {@code app.admin.*} — used by AdminBootstrap to seed an ADMIN on first startup. */
@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        boolean enabled,
        String email,
        String password,
        String name
) {
}
