package com.fitness.fittrackpro.dto.trainer;

import com.fitness.fittrackpro.model.Trainer;

import java.time.LocalDateTime;

public record TrainerResponse(
        Long id,
        String name,
        String email,
        String specialization,
        Integer experienceYears,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TrainerResponse from(Trainer trainer) {
        return new TrainerResponse(
                trainer.getId(),
                trainer.getName(),
                trainer.getEmail(),
                trainer.getSpecialization(),
                trainer.getExperienceYears(),
                trainer.getCreatedAt(),
                trainer.getUpdatedAt()
        );
    }
}
