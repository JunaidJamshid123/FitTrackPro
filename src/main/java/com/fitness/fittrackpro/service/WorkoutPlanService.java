package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.workoutplan.WorkoutPlanRequest;
import com.fitness.fittrackpro.dto.workoutplan.WorkoutPlanResponse;
import com.fitness.fittrackpro.exception.WorkoutPlanNotFoundException;
import com.fitness.fittrackpro.model.Trainer;
import com.fitness.fittrackpro.model.WorkoutPlan;
import com.fitness.fittrackpro.repository.WorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;

    @Transactional(readOnly = true)
    public Page<WorkoutPlanResponse> search(Long trainerId, Boolean published, String q, Pageable pageable) {
        String trimmedQ = (q == null || q.isBlank()) ? null : q.trim();
        return workoutPlanRepository.search(trainerId, published, trimmedQ, pageable)
                .map(WorkoutPlanResponse::from);
    }

    @Transactional(readOnly = true)
    public WorkoutPlanResponse getById(Long id) {
        return WorkoutPlanResponse.from(loadWorkoutPlan(id));
    }

    @Transactional
    public WorkoutPlanResponse create(WorkoutPlanRequest req, Trainer trainer) {
        WorkoutPlan plan = new WorkoutPlan();
        apply(plan, req);
        plan.setTrainer(trainer);
        return WorkoutPlanResponse.from(workoutPlanRepository.save(plan));
    }

    @Transactional
    public WorkoutPlanResponse update(Long id, WorkoutPlanRequest req) {
        WorkoutPlan plan = loadWorkoutPlan(id);
        apply(plan, req);
        return WorkoutPlanResponse.from(plan);
    }

    @Transactional
    public void delete(Long id) {
        if (!workoutPlanRepository.existsById(id)) {
            throw new WorkoutPlanNotFoundException(id);
        }
        workoutPlanRepository.deleteById(id);
    }

    @Transactional
    public WorkoutPlanResponse togglePublish(Long id) {
        WorkoutPlan plan = loadWorkoutPlan(id);
        plan.setPublished(!plan.getPublished());
        return WorkoutPlanResponse.from(plan);
    }

    private void apply(WorkoutPlan plan, WorkoutPlanRequest req) {
        plan.setName(req.name().trim());
        plan.setDescription(req.description());
        plan.setDurationWeeks(req.durationWeeks());
        plan.setDifficulty(req.difficulty());
        plan.setGoal(req.goal());
        if (req.published() != null) {
            plan.setPublished(req.published());
        }
    }

    private WorkoutPlan loadWorkoutPlan(Long id) {
        return workoutPlanRepository.findById(id)
                .orElseThrow(() -> new WorkoutPlanNotFoundException(id));
    }
}
