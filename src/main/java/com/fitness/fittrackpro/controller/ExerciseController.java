package com.fitness.fittrackpro.controller;

import com.fitness.fittrackpro.dto.exercise.ExerciseRequest;
import com.fitness.fittrackpro.dto.exercise.ExerciseResponse;
import com.fitness.fittrackpro.model.enums.Difficulty;
import com.fitness.fittrackpro.model.enums.MuscleGroup;
import com.fitness.fittrackpro.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<Page<ExerciseResponse>> list(
            @RequestParam(required = false) MuscleGroup muscleGroup,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String equipment,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(exerciseService.search(muscleGroup, difficulty, equipment, q, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody ExerciseRequest req) {
        return ResponseEntity.ok(exerciseService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
