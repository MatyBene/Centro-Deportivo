package com.utn.API_CentroDeportivo.model.entity.routine;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "muscle_group")
    private String muscleGroup;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "suggested_weight")
    private String suggestedWeight;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(name = "exercise_order")
    private Integer exerciseOrder;

    @ElementCollection
    @CollectionTable(name = "exercise_series_repetitions",
            joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "series_repetition")
    private List<String> seriesRepetitions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_day_id")
    private RoutineDay routineDay;
}