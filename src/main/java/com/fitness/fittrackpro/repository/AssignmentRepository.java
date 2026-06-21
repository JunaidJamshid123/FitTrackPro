package com.fitness.fittrackpro.repository;

import com.fitness.fittrackpro.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("select a from Assignment a where a.trainer.id = :trainerId")
    List<Assignment> findByTrainerId(@Param("trainerId") Long trainerId);

    Optional<Assignment> findByTrainerIdAndUserId(Long trainerId, Long userId);

    void deleteByTrainerIdAndUserId(Long trainerId, Long userId);
}
