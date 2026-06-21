package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.workoutsession.WorkoutSessionRequest;
import com.fitness.fittrackpro.dto.workoutsession.WorkoutSessionResponse;
import com.fitness.fittrackpro.exception.WorkoutSessionNotFoundException;
import com.fitness.fittrackpro.exception.WorkoutPlanNotFoundException;
import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.WorkoutPlan;
import com.fitness.fittrackpro.model.WorkoutSession;
import com.fitness.fittrackpro.repository.WorkoutPlanRepository;
import com.fitness.fittrackpro.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutPlanRepository workoutPlanRepository;

    @Transactional(readOnly = true)
    public Page<WorkoutSessionResponse> getUserSessions(Long userId, Long planId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return workoutSessionRepository.findByUserId(userId, planId, fromDate, toDate, pageable)
                .map(WorkoutSessionResponse::from);
    }

    @Transactional(readOnly = true)
    public WorkoutSessionResponse getById(Long id) {
        return WorkoutSessionResponse.from(loadWorkoutSession(id));
    }

    @Transactional
    public WorkoutSessionResponse create(WorkoutSessionRequest req, User user) {
        WorkoutSession session = new WorkoutSession();
        apply(session, req);
        session.setUser(user);
        
        if (req.workoutPlanId() != null) {
            WorkoutPlan plan = workoutPlanRepository.findById(req.workoutPlanId())
                    .orElseThrow(() -> new WorkoutPlanNotFoundException(req.workoutPlanId()));
            session.setPlan(plan);
        }
        
        return WorkoutSessionResponse.from(workoutSessionRepository.save(session));
    }

    @Transactional
    public WorkoutSessionResponse update(Long id, WorkoutSessionRequest req) {
        WorkoutSession session = loadWorkoutSession(id);
        apply(session, req);
        
        if (req.workoutPlanId() != null && !req.workoutPlanId().equals(session.getPlan().getId())) {
            WorkoutPlan plan = workoutPlanRepository.findById(req.workoutPlanId())
                    .orElseThrow(() -> new WorkoutPlanNotFoundException(req.workoutPlanId()));
            session.setPlan(plan);
        }
        
        return WorkoutSessionResponse.from(session);
    }

    @Transactional
    public void delete(Long id) {
        if (!workoutSessionRepository.existsById(id)) {
            throw new WorkoutSessionNotFoundException(id);
        }
        workoutSessionRepository.deleteById(id);
    }

    private void apply(WorkoutSession session, WorkoutSessionRequest req) {
        session.setSessionDate(req.sessionDate());
        session.setDurationMinutes(req.durationMinutes());
        session.setCaloriesBurned(req.caloriesBurned() != null ? req.caloriesBurned() : 0);
    }

    private WorkoutSession loadWorkoutSession(Long id) {
        return workoutSessionRepository.findById(id)
                .orElseThrow(() -> new WorkoutSessionNotFoundException(id));
    }
}
