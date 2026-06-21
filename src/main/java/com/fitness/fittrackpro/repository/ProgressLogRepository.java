package com.fitness.fittrackpro.repository;

import com.fitness.fittrackpro.model.ProgressLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {

    @Query("""
            select p from ProgressLog p
            where p.user.id = :userId
              and (:fromDate is null or p.logDate >= :fromDate)
              and (:toDate is null or p.logDate <= :toDate)
            order by p.logDate desc
            """)
    Page<ProgressLog> findByUserId(@Param("userId") Long userId,
                                   @Param("fromDate") LocalDate fromDate,
                                   @Param("toDate") LocalDate toDate,
                                   Pageable pageable);

    List<ProgressLog> findByUserIdOrderByLogDateDesc(Long userId);

    Optional<ProgressLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    List<ProgressLog> findByUserIdAndLogDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    long countByUserId(Long userId);
}
