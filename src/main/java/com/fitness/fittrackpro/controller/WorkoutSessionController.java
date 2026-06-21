package com.fitness.fittrackpro.controller;

import com.fitness.fittrackpro.dto.workoutsession.WorkoutSessionRequest;
import com.fitness.fittrackpro.dto.workoutsession.WorkoutSessionResponse;
import com.fitness.fittrackpro.security.CustomUserDetails;
import com.fitness.fittrackpro.service.WorkoutSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/workout-sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    @GetMapping
    public ResponseEntity<Page<WorkoutSessionResponse>> getMyWorkouts(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20, sort = "sessionDate", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(workoutSessionService.getUserSessions(
                principal.getId(), planId, fromDate, toDate, pageable
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> create(
            @Valid @RequestBody WorkoutSessionRequest req,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workoutSessionService.create(req, principal.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutSessionRequest req
    ) {
        return ResponseEntity.ok(workoutSessionService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workoutSessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
