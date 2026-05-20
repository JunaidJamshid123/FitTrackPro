package com.fitness.fittrackpro.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;

/**
 * Join entity for the N—N relationship between {@link WorkoutPlan} and {@link Exercise},
 * carrying per-exercise attributes (sets, reps, restSeconds, dayOfWeek).
 */
@Entity
@Table(
        name = "plan_exercises",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_plan_exercise_day",
                columnNames = {"plan_id", "exercise_id", "day_of_week"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"plan", "exercise"})
public class PlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private WorkoutPlan plan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private Integer sets;

    @Column(nullable = false)
    private Integer reps;

    @Column(name = "rest_seconds", nullable = false)
    private Integer restSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;
}
