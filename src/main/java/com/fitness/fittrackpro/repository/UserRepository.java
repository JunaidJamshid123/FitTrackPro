package com.fitness.fittrackpro.repository;

import com.fitness.fittrackpro.model.User;
import com.fitness.fittrackpro.model.enums.Goal;
import com.fitness.fittrackpro.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            select u from User u
            where (:role is null or u.role = :role)
              and (:goal is null or u.goal = :goal)
              and (:q is null or lower(u.name) like lower(concat('%', :q, '%'))
                              or lower(u.email) like lower(concat('%', :q, '%')))
            """)
    Page<User> search(@Param("role") Role role,
                      @Param("goal") Goal goal,
                      @Param("q") String q,
                      Pageable pageable);
}
