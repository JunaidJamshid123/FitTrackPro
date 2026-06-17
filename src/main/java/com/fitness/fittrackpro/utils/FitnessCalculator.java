package com.fitness.fittrackpro.utils;

import com.fitness.fittrackpro.model.enums.ActivityLevel;
import com.fitness.fittrackpro.model.enums.Gender;
import com.fitness.fittrackpro.model.enums.Goal;

/**
 * Pure helpers for BMI and recommended-daily-calorie computations.
 * Uses Mifflin-St Jeor for BMR, multiplied by an activity factor (TDEE),
 * then nudged by the user's {@link Goal}.
 */
public final class FitnessCalculator {

    private FitnessCalculator() {
    }

    /** BMI = weight(kg) / height(m)^2 — rounded to 1 decimal. */
    public static double bmi(double weightKg, double heightCm) {
        double m = heightCm / 100.0;
        return Math.round((weightKg / (m * m)) * 10.0) / 10.0;
    }

    /** WHO BMI categories. */
    public static String bmiCategory(double bmi) {
        if (bmi < 18.5) return "UNDERWEIGHT";
        if (bmi < 25.0) return "NORMAL";
        if (bmi < 30.0) return "OVERWEIGHT";
        return "OBESE";
    }

    /**
     * Recommended daily calorie intake = BMR × activity factor + goal adjustment.
     */
    public static int recommendedDailyCalories(int age, Gender gender, double heightCm,
                                               double weightKg, ActivityLevel activity, Goal goal) {
        double bmr = bmr(age, gender, heightCm, weightKg);
        double tdee = bmr * activityFactor(activity);
        return (int) Math.round(tdee + goalAdjustment(goal));
    }

    private static double bmr(int age, Gender gender, double heightCm, double weightKg) {
        double base = 10 * weightKg + 6.25 * heightCm - 5 * age;
        return switch (gender) {
            case MALE -> base + 5;
            case FEMALE -> base - 161;
            case OTHER -> base - 78; // average of MALE (+5) and FEMALE (-161)
        };
    }

    private static double activityFactor(ActivityLevel a) {
        return switch (a) {
            case SEDENTARY -> 1.2;
            case LIGHTLY_ACTIVE -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case VERY_ACTIVE -> 1.725;
            case EXTRA_ACTIVE -> 1.9;
        };
    }

    private static int goalAdjustment(Goal g) {
        return switch (g) {
            case WEIGHT_LOSS -> -500;
            case MUSCLE_GAIN -> 300;
            case ENDURANCE -> 200;
            case MAINTENANCE -> 0;
        };
    }
}
