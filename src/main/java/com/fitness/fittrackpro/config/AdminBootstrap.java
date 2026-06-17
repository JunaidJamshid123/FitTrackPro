package com.fitness.fittrackpro.config;

import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.enums.Gender;
import com.fitness.fittrackpro.model.enums.Goal;
import com.fitness.fittrackpro.model.enums.Role;
import com.fitness.fittrackpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds a single ADMIN account on application startup when one with the
 * configured email does not already exist. Toggle off with {@code app.admin.enabled=false}.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminBootstrap {

    private final AdminProperties props;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedAdmin() {
        return args -> run();
    }

    @Transactional
    public void run() {
        if (!props.enabled()) {
            log.info("Admin bootstrap disabled (app.admin.enabled=false)");
            return;
        }
        if (props.email() == null || props.email().isBlank()
                || props.password() == null || props.password().isBlank()) {
            log.warn("Admin bootstrap skipped: app.admin.email or app.admin.password is missing");
            return;
        }

        String email = props.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            log.info("Admin bootstrap: user '{}' already exists, nothing to do", email);
            return;
        }

        User admin = new User();
        admin.setName(props.name() == null || props.name().isBlank() ? "Admin" : props.name().trim());
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(props.password()));
        admin.setAge(30);
        admin.setGender(Gender.OTHER);
        admin.setHeight(170.0);
        admin.setWeight(70.0);
        admin.setGoal(Goal.MAINTENANCE);
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        admin.setAccountNonLocked(true);

        userRepository.save(admin);
        log.info("Admin bootstrap: created ADMIN user '{}'", email);
    }
}
