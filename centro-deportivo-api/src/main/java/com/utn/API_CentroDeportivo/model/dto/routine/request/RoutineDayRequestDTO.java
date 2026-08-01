package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class RoutineDayRequestDTO {
    private Long id;
    private Integer dayNumber;
    private String name;
    private String description;
    private Integer order;
    private List<ExerciseRequestDTO> exercises;
}