package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.trainer.TrainerRequest;
import com.fitness.fittrackpro.exception.EmailAlreadyUsedException;
import com.fitness.fittrackpro.model.Trainer;
import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.repository.AssignmentRepository;
import com.fitness.fittrackpro.repository.TrainerRepository;
import com.fitness.fittrackpro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void create_shouldRejectDuplicateEmailAcrossUserAndTrainer() {
        TrainerRequest request = new TrainerRequest(
                "Alice Trainer",
                "trainer@example.com",
                "Strength",
                5
        );

        when(trainerRepository.findByEmail("trainer@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("trainer@example.com")).thenReturn(true);

        assertThatThrownBy(() -> trainerService.create(request))
                .isInstanceOf(EmailAlreadyUsedException.class);

        verify(trainerRepository, never()).save(any(Trainer.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
