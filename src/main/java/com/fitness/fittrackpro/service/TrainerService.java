package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.trainer.TrainerRequest;
import com.fitness.fittrackpro.dto.trainer.TrainerResponse;
import com.fitness.fittrackpro.dto.trainer.TrainerUserResponse;
import com.fitness.fittrackpro.exception.TrainerNotFoundException;
import com.fitness.fittrackpro.exception.UserNotFoundException;
import com.fitness.fittrackpro.model.Assignment;
import com.fitness.fittrackpro.model.Trainer;
import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.enums.Gender;
import com.fitness.fittrackpro.model.enums.Goal;
import com.fitness.fittrackpro.model.enums.Role;
import com.fitness.fittrackpro.repository.AssignmentRepository;
import com.fitness.fittrackpro.repository.TrainerRepository;
import com.fitness.fittrackpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<TrainerResponse> list(Pageable pageable) {
        return trainerRepository.findAll(pageable).map(TrainerResponse::from);
    }

    @Transactional(readOnly = true)
    public TrainerResponse getById(Long id) {
        return TrainerResponse.from(loadTrainer(id));
    }

    @Transactional
    public TrainerResponse create(TrainerRequest req) {
        // Create Trainer record
        Trainer trainer = new Trainer();
        apply(trainer, req);
        trainer = trainerRepository.save(trainer);

        // Also create corresponding User record with role=TRAINER
        User user = new User();
        user.setName(req.name().trim());
        user.setEmail(req.email().trim().toLowerCase());
        // Generate a temporary password (trainer should change it)
        user.setPassword(passwordEncoder.encode("TempPassword123"));
        user.setAge(30); // Default values for trainer user
        user.setGender(Gender.MALE);
        user.setHeight(170.0);
        user.setWeight(70.0);
        user.setGoal(Goal.MAINTENANCE);
        user.setRole(Role.TRAINER);
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        
        userRepository.save(user);

        return TrainerResponse.from(trainer);
    }

    @Transactional
    public TrainerResponse update(Long id, TrainerRequest req) {
        Trainer trainer = loadTrainer(id);
        apply(trainer, req);
        return TrainerResponse.from(trainer);
    }

    @Transactional
    public void delete(Long id) {
        if (!trainerRepository.existsById(id)) {
            throw new TrainerNotFoundException(id);
        }
        trainerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TrainerUserResponse> getAssignedUsers(Long trainerId) {
        Trainer trainer = loadTrainer(trainerId);
        List<Assignment> assignments = assignmentRepository.findByTrainerId(trainerId);
        return assignments.stream()
                .map(a -> TrainerUserResponse.from(a.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignUserToTrainer(Long trainerId, Long userId) {
        Trainer trainer = loadTrainer(trainerId);
        User user = loadUser(userId);

        // Check if assignment already exists
        if (assignmentRepository.findByTrainerIdAndUserId(trainerId, userId).isPresent()) {
            return; // Assignment already exists
        }

        Assignment assignment = new Assignment();
        assignment.setTrainer(trainer);
        assignment.setUser(user);
        assignmentRepository.save(assignment);
    }

    @Transactional
    public void unassignUserFromTrainer(Long trainerId, Long userId) {
        loadTrainer(trainerId); // Verify trainer exists
        loadUser(userId);      // Verify user exists

        assignmentRepository.deleteByTrainerIdAndUserId(trainerId, userId);
    }

    private void apply(Trainer trainer, TrainerRequest req) {
        trainer.setName(req.name().trim());
        trainer.setEmail(req.email().trim());
        trainer.setSpecialization(req.specialization().trim());
        trainer.setExperienceYears(req.experienceYears());
    }

    private Trainer loadTrainer(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(id));
    }

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
