package com.fitness.fittrackpro.model;

import com.fitness.fittrackpro.model.enums.ActivityLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false, length = 30)
    private ActivityLevel activityLevel;

    /** Target weight in kilograms. */
    @Column(name = "target_weight")
    private Double targetWeight;

    /** Target daily calories. */
    @Column(name = "target_calories")
    private Integer targetCalories;

    @Column(length = 500)
    private String bio;
}
