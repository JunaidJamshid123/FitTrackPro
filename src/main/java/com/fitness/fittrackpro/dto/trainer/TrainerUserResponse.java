package com.fitness.fittrackpro.dto.trainer;

import com.fitness.fittrackpro.model.User;

public record TrainerUserResponse(
        Long userId,
        String name,
        String email,
        Integer age,
        Double weight,
        Double height
) {
    public static TrainerUserResponse from(User user) {
        return new TrainerUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getWeight(),
                user.getHeight()
        );
    }
}
