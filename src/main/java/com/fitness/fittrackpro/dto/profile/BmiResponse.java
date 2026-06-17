package com.fitness.fittrackpro.dto.profile;

public record BmiResponse(
        Double heightCm,
        Double weightKg,
        Double bmi,
        String category,
        Integer recommendedDailyCalories
) {
}
