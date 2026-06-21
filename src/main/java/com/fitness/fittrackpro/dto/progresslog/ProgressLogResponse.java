package com.fitness.fittrackpro.dto.progresslog;

import com.fitness.fittrackpro.model.ProgressLog;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProgressLogResponse(
        Long id,
        LocalDate logDate,
        Double weight,
        Double bodyFatPercent,
        Double chest,
        Double waist,
        Double hips,
        String notes,
        Long userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProgressLogResponse from(ProgressLog log) {
        return new ProgressLogResponse(
                log.getId(),
                log.getLogDate(),
                log.getWeight(),
                log.getBodyFatPercent(),
                log.getChest(),
                log.getWaist(),
                log.getHips(),
                log.getNotes(),
                log.getUser().getId(),
                log.getCreatedAt(),
                log.getUpdatedAt()
        );
    }
}
