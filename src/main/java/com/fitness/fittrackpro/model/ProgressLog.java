package com.fitness.fittrackpro.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "progress_logs",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_progress_user_week",
                columnNames = {"user_id", "log_date"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class ProgressLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    /** Weight in kilograms. */
    @Column(nullable = false)
    private Double weight;

    @Column(name = "body_fat_percent")
    private Double bodyFatPercent;

    /** Chest measurement (cm). */
    private Double chest;

    /** Waist measurement (cm). */
    private Double waist;

    /** Hips measurement (cm). */
    private Double hips;

    @Column(length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ----- Relationships -----

    // ProgressLog N—1 User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
