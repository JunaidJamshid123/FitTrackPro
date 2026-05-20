package com.fitness.fittrackpro.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Per-exercise actuals logged for a {@link WorkoutSession}.
 */
@Entity
@Table(name = "session_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"session", "exercise"})
public class SessionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "sets_done", nullable = false)
    private Integer setsDone;

    @Column(name = "reps_done", nullable = false)
    private Integer repsDone;

    /** Weight used (kg). */
    @Column(name = "weight_used", nullable = false)
    private Double weightUsed;
}
