package com.fitness.fittrackpro.controller;

import com.fitness.fittrackpro.dto.workoutplan.WorkoutPlanRequest;
import com.fitness.fittrackpro.dto.workoutplan.WorkoutPlanResponse;
import com.fitness.fittrackpro.exception.WorkoutPlanNotFoundException;
import com.fitness.fittrackpro.model.Trainer;
import com.fitness.fittrackpro.repository.TrainerRepository;
import com.fitness.fittrackpro.repository.WorkoutPlanRepository;
import com.fitness.fittrackpro.security.CustomUserDetails;
import com.fitness.fittrackpro.service.WorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workout-plans")
@RequiredArgsConstructor
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final TrainerRepository trainerRepository;

    @GetMapping
    public ResponseEntity<Page<WorkoutPlanResponse>> list(
            @RequestParam(required = false) Long trainerId,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(workoutPlanService.search(trainerId, published, q, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutPlanResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutPlanService.getById(id));
    }

    @PostMapping
    
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<?> create(
            @Valid @RequestBody WorkoutPlanRequest req,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        // Find trainer by email (assuming trainers have corresponding trainer records)
        Trainer trainer = trainerRepository.findByEmail(principal.getUser().getEmail())
                .orElse(null);
        
        if (trainer == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No trainer profile found for user: " + principal.getUser().getEmail()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workoutPlanService.create(req, trainer));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<WorkoutPlanResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutPlanRequest req,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        // Verify ownership or admin
        if (!isTrainerOwnerOrAdmin(id, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(workoutPlanService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        // Verify ownership or admin
        if (!isTrainerOwnerOrAdmin(id, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        workoutPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<WorkoutPlanResponse> togglePublish(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        // Verify ownership or admin
        if (!isTrainerOwnerOrAdmin(id, principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(workoutPlanService.togglePublish(id));
    }

    private boolean isTrainerOwnerOrAdmin(Long planId, CustomUserDetails principal) {
        if (principal.getUser().getRole().name().equals("ADMIN")) {
            return true;
        }
        // Check if the plan belongs to the trainer
        return workoutPlanRepository.existsByIdAndTrainerId(planId, principal.getId());
    }
}
