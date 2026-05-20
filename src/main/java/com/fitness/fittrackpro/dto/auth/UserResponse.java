package com.fitness.fittrackpro.dto.auth;

import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.enums.Gender;
import com.fitness.fittrackpro.model.enums.Goal;
import com.fitness.fittrackpro.model.enums.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Integer age,
        Gender gender,
        Double height,
        Double weight,
        Goal goal,
        Role role
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getName(), u.getEmail(), u.getAge(),
                u.getGender(), u.getHeight(), u.getWeight(),
                u.getGoal(), u.getRole()
        );
    }
}
