package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.auth.*;
import com.fitness.fittrackpro.exception.EmailAlreadyUsedException;
import com.fitness.fittrackpro.exception.InvalidTokenException;
import com.fitness.fittrackpro.model.RefreshToken;
import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.enums.Role;
import com.fitness.fittrackpro.repository.RefreshTokenRepository;
import com.fitness.fittrackpro.repository.UserRepository;
import com.fitness.fittrackpro.security.JwtProperties;
import com.fitness.fittrackpro.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    // ---------- REGISTER ----------

    @Transactional
    public AuthResponse register(RegisterRequest req, HttpServletRequest http) {
        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyUsedException(req.email());
        }

        User user = new User();
        user.setName(req.name().trim());
        user.setEmail(req.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setAge(req.age());
        user.setGender(req.gender());
        user.setHeight(req.height());
        user.setWeight(req.weight());
        user.setGoal(req.goal());
        // If role is specified and is TRAINER or ADMIN, use it; otherwise default to USER
        user.setRole(req.role() != null && (req.role() == Role.TRAINER || req.role() == Role.ADMIN) ? req.role() : Role.USER);
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        user = userRepository.save(user);
        log.info("Registered new user id={} email={} role={}", user.getId(), user.getEmail(), user.getRole());

        return issueTokens(user, http);
    }

    // ---------- LOGIN ----------

    @Transactional
    public AuthResponse login(LoginRequest req, HttpServletRequest http) {
        // Generic message for both "no such user" and "wrong password" — do not leak which.
        User user = userRepository.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        ensureNotLocked(user);

        if (!user.isEnabled()) {
            throw new LockedException("Account is disabled");
        }

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            registerFailedAttempt(user);
            throw new BadCredentialsException("Invalid email or password");
        }

        // Success — reset failure counter
        if (user.getFailedLoginAttempts() != 0 || user.getLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setAccountNonLocked(true);
        }

        log.info("User logged in id={} email={}", user.getId(), user.getEmail());
        return issueTokens(user, http);
    }

    private void ensureNotLocked(User user) {
        Instant lockedUntil = user.getLockedUntil();
        if (lockedUntil != null && lockedUntil.isAfter(Instant.now())) {
            throw new LockedException("Account temporarily locked. Try again later.");
        }
        // Lock has expired — clear it
        if (lockedUntil != null) {
            user.setLockedUntil(null);
            user.setAccountNonLocked(true);
            user.setFailedLoginAttempts(0);
        }
    }

    private void registerFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setLockedUntil(Instant.now().plus(LOCK_MINUTES, ChronoUnit.MINUTES));
            log.warn("Account locked due to {} failed login attempts: userId={}", attempts, user.getId());
        }
    }

    // ---------- REFRESH (rotation + reuse detection) ----------

    @Transactional
    public AuthResponse refresh(RefreshRequest req, HttpServletRequest http) {
        Claims claims = jwtService.parse(req.refreshToken());
        if (claims == null || !jwtService.isRefreshToken(claims)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String hash = sha256(req.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> {
                    // Token signature was valid but it's not in our DB — possible reuse of a rotated token.
                    // Defensive measure: revoke ALL of this user's tokens.
                    Long userId = jwtService.extractUserId(claims);
                    userRepository.findById(userId).ifPresent(refreshTokenRepository::revokeAllForUser);
                    return new InvalidTokenException("Refresh token reuse detected — all sessions revoked");
                });

        if (!stored.isActive()) {
            // Reuse of a revoked token — revoke everything for this user as a precaution.
            refreshTokenRepository.revokeAllForUser(stored.getUser());
            throw new InvalidTokenException("Refresh token is no longer valid");
        }

        // Rotate: revoke the old, issue a new pair
        stored.setRevoked(true);
        User user = stored.getUser();

        return issueTokens(user, http);
    }

    // ---------- LOGOUT ----------

    @Transactional
    public void logout(RefreshRequest req) {
        if (req == null || req.refreshToken() == null) return;
        String hash = sha256(req.refreshToken());
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> rt.setRevoked(true));
    }

    // ---------- helpers ----------

    private AuthResponse issueTokens(User user, HttpServletRequest http) {
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        RefreshToken record = RefreshToken.builder()
                .user(user)
                .tokenHash(sha256(refresh))
                .expiresAt(Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs()))
                .revoked(false)
                .userAgent(safeHeader(http, "User-Agent", 255))
                .ipAddress(clientIp(http))
                .build();
        refreshTokenRepository.save(record);

        long expiresInSec = jwtProperties.accessTokenExpirationMs() / 1000;
        return AuthResponse.of(access, refresh, expiresInSec, UserResponse.from(user));
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String safeHeader(HttpServletRequest req, String name, int max) {
        if (req == null) return null;
        String v = req.getHeader(name);
        if (v == null) return null;
        return v.length() > max ? v.substring(0, max) : v;
    }

    private static String clientIp(HttpServletRequest req) {
        if (req == null) return null;
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }
        return req.getRemoteAddr();
    }
}
