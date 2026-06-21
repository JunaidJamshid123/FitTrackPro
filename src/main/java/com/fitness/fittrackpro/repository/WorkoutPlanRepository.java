package com.fitness.fittrackpro.repository;

import com.fitness.fittrackpro.model.WorkoutPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    @Query("""
            select p from WorkoutPlan p
            where (:trainerId is null or p.trainer.id = :trainerId)
              and (:published is null or p.published = :published)
              and (lower(p.name) like lower(concat('%', :q, '%')) or :q is null)
            """)
    Page<WorkoutPlan> search(@Param("trainerId") Long trainerId,
                             @Param("published") Boolean published,
                             @Param("q") String q,
                             Pageable pageable);

    boolean existsByIdAndTrainerId(Long planId, Long trainerId);
}
