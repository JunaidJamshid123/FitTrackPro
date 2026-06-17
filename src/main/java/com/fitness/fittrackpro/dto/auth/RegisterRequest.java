package com.fitness.fittrackpro.dto.auth;

import com.fitness.fittrackpro.model.enums.Gender;
import com.fitness.fittrackpro.model.enums.Goal;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "Password must contain at least one letter and one digit"
        )
        String password,
        @NotNull @Min(10) @Max(120) Integer age,
        @NotNull Gender gender,
        @NotNull @DecimalMin("50.0") @DecimalMax("260.0") Double height,
        @NotNull @DecimalMin("20.0") @DecimalMax("400.0") Double weight,
        @NotNull Goal goal
) {

}
