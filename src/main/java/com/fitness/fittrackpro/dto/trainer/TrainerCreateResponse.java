package com.fitness.fittrackpro.dto.trainer;

import com.fitness.fittrackpro.model.Trainer;
import com.fitness.fittrackpro.model.User;

import java.time.LocalDateTime;

/**
 * Response DTO for trainer creation that includes the temporary password
 * so the admin can communicate it to the newly created trainer.
 */
public record TrainerCreateResponse(
        Long id,
        String name,
        String email,
        String specialization,
        Integer experienceYears,
        String temporaryPassword,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TrainerCreateResponse from(Trainer trainer, String temporaryPassword) {
        return new TrainerCreateResponse(
                trainer.getId(),
                trainer.getName(),
                trainer.getEmail(),
                trainer.getSpecialization(),
                trainer.getExperienceYears(),
                temporaryPassword,
                trainer.getCreatedAt(),
                trainer.getUpdatedAt()
        );
    }
}
