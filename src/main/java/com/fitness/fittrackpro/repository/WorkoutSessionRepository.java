package com.fitness.fittrackpro.repository;

import com.fitness.fittrackpro.model.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    @Query("""
            select s from WorkoutSession s
            where s.user.id = :userId
              and (:planId is null or s.plan.id = :planId)
              and (:fromDate is null or s.sessionDate >= :fromDate)
              and (:toDate is null or s.sessionDate <= :toDate)
            order by s.sessionDate desc
            """)
    Page<WorkoutSession> findByUserId(@Param("userId") Long userId,
                                      @Param("planId") Long planId,
                                      @Param("fromDate") LocalDate fromDate,
                                      @Param("toDate") LocalDate toDate,
                                      Pageable pageable);

    List<WorkoutSession> findByUserIdAndSessionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    long countByUserId(Long userId);
}
