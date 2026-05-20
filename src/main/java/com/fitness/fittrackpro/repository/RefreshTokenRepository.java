package com.fitness.fittrackpro.repository;

import com.fitness.fittrackpro.model.RefreshToken;
import com.fitness.fittrackpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.user = :user and r.revoked = false")
    int revokeAllForUser(@Param("user") User user);
}
