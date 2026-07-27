package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class RoutineDayRequestDTO {
    private Integer dayOrder;
    private String day;
    private List<ExerciseRequestDTO> exercises;
}