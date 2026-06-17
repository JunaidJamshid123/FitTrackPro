package com.fitness.fittrackpro.service;

import com.fitness.fittrackpro.dto.exercise.ExerciseRequest;
import com.fitness.fittrackpro.dto.exercise.ExerciseResponse;
import com.fitness.fittrackpro.exception.ExerciseNotFoundException;
import com.fitness.fittrackpro.model.Exercise;
import com.fitness.fittrackpro.model.enums.Difficulty;
import com.fitness.fittrackpro.model.enums.MuscleGroup;
import com.fitness.fittrackpro.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Transactional(readOnly = true)
    public Page<ExerciseResponse> search(MuscleGroup muscleGroup, Difficulty difficulty,
                                         String equipment, String q, Pageable pageable) {
        String trimmedQ = (q == null || q.isBlank()) ? null : q.trim();
        String trimmedEq = (equipment == null || equipment.isBlank()) ? null : equipment.trim();
        return exerciseRepository.search(muscleGroup, difficulty, trimmedEq, trimmedQ, pageable)
                .map(ExerciseResponse::from);
    }

    @Transactional(readOnly = true)
    public ExerciseResponse getById(Long id) {
        return ExerciseResponse.from(loadExercise(id));
    }

    @Transactional
    public ExerciseResponse create(ExerciseRequest req) {
        Exercise e = new Exercise();
        apply(e, req);
        return ExerciseResponse.from(exerciseRepository.save(e));
    }

    @Transactional
    public ExerciseResponse update(Long id, ExerciseRequest req) {
        Exercise e = loadExercise(id);
        apply(e, req);
        return ExerciseResponse.from(e);
    }

    @Transactional
    public void delete(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ExerciseNotFoundException(id);
        }
        exerciseRepository.deleteById(id);
    }

    private void apply(Exercise e, ExerciseRequest req) {
        e.setName(req.name().trim());
        e.setDescription(req.description());
        e.setMuscleGroup(req.muscleGroup());
        e.setDifficulty(req.difficulty());
        e.setEquipment(req.equipment());
    }

    private Exercise loadExercise(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(id));
    }
}
