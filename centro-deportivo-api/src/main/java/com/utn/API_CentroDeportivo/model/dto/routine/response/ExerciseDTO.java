package com.utn.API_CentroDeportivo.model.dto.routine.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class ExerciseDTO {
    private Long id;
    private String name;
    private String muscleGroup;
    private String type;
    private String notes;
    private String suggestedWeight;
    private Integer restSeconds;
    private Integer exerciseOrder;
    private List<SeriesRepetitionDTO> seriesRepetitions;
}