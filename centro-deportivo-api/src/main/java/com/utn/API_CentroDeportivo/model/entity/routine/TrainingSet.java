package com.utn.API_CentroDeportivo.model.entity.routine;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "training_set")
public class TrainingSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;
    private Integer weight;
    private Integer repetitions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_history_id")
    private TrainingHistory trainingHistory;
}