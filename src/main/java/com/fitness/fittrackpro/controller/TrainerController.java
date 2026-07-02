package com.fitness.fittrackpro.controller;

import com.fitness.fittrackpro.dto.trainer.TrainerCreateResponse;
import com.fitness.fittrackpro.dto.trainer.TrainerRequest;
import com.fitness.fittrackpro.dto.trainer.TrainerResponse;
import com.fitness.fittrackpro.dto.trainer.TrainerUserResponse;
import com.fitness.fittrackpro.security.CustomUserDetails;
import com.fitness.fittrackpro.service.TrainerService;
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

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public ResponseEntity<Page<TrainerResponse>> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(trainerService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerCreateResponse> create(@Valid @RequestBody TrainerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainerService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TrainerRequest req
    ) {
        return ResponseEntity.ok(trainerService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trainerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/users")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<TrainerUserResponse>> getMyUsers(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        // Get trainer ID by email
        Long trainerId = principal.getId(); // Assuming trainer ID = user ID (simplified)
        // In production, you'd fetch the actual trainer ID linked to this user
        return ResponseEntity.ok(trainerService.getAssignedUsers(trainerId));
    }

    @GetMapping("/{trainerId}/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TrainerUserResponse>> getTrainerUsers(@PathVariable Long trainerId) {
        return ResponseEntity.ok(trainerService.getAssignedUsers(trainerId));
    }

    @PostMapping("/{trainerId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignUserToTrainer(
            @PathVariable Long trainerId,
            @PathVariable Long userId
    ) {
        trainerService.assignUserToTrainer(trainerId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{trainerId}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unassignUserFromTrainer(
            @PathVariable Long trainerId,
            @PathVariable Long userId
    ) {
        trainerService.unassignUserFromTrainer(trainerId, userId);
        return ResponseEntity.noContent().build();
    }
}
