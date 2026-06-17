package com.fitness.fittrackpro.dto.profile;

import com.fitness.fittrackpro.model.UserProfile;
import com.fitness.fittrackpro.model.enums.ActivityLevel;

public record ProfileResponse(
        Long userId,
        ActivityLevel activityLevel,
        Double targetWeight,
        Integer targetCalories,
        String bio
) {
    public static ProfileResponse from(UserProfile p) {
        return new ProfileResponse(
                p.getUser().getId(),
                p.getActivityLevel(),
                p.getTargetWeight(),
                p.getTargetCalories(),
                p.getBio()
        );
    }
}
