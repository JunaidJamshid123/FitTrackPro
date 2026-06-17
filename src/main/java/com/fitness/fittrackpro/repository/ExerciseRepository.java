package com.fitness.fittrackpro.repository;

import com.fitness.fittrackpro.model.Exercise;
import com.fitness.fittrackpro.model.enums.Difficulty;
import com.fitness.fittrackpro.model.enums.MuscleGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("""
            select e from Exercise e
            where (:muscleGroup is null or e.muscleGroup = :muscleGroup)
              and (:difficulty  is null or e.difficulty  = :difficulty)
              and (:equipment   is null or lower(e.equipment) = lower(:equipment))
              and (:q           is null or lower(e.name) like lower(concat('%', :q, '%')))
            """)
    Page<Exercise> search(@Param("muscleGroup") MuscleGroup muscleGroup,
                          @Param("difficulty") Difficulty difficulty,
                          @Param("equipment") String equipment,
                          @Param("q") String q,
                          Pageable pageable);
}
