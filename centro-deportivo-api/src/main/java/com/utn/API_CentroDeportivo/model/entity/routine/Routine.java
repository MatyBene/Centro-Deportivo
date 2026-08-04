package com.utn.API_CentroDeportivo.model.entity.routine;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by_username")
    private String createdByUsername;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String level;
    private String goal;

    @Column(name = "duration_weeks")
    private Integer durationWeeks;

    @Column(name = "days_per_week")
    private Integer daysPerWeek;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDate.now();
        }
    }

    @Column(name = "is_template")
    private boolean isTemplate;

    @Embedded
    private Warmup warmup;

    @Embedded
    private Cooldown cooldown;

    @ElementCollection
    @CollectionTable(name = "routine_general_notes",
            joinColumns = @JoinColumn(name = "routine_id"))
    @Column(name = "note")
    private List<String> generalNotes = new ArrayList<>();

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineDay> routineDays = new ArrayList<>();
}