package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class ExerciseRequestDTO {
    private String name;
    private String muscleGroup;
    private String type;
    private String notes;
    private String suggestedWeight;
    private Integer restSeconds;
    private Integer exerciseOrder;
    private List<SeriesRepetitionRequestDTO> seriesRepetitions;
}

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
class SeriesRepetitionRequestDTO {
    private String repetitions;
}