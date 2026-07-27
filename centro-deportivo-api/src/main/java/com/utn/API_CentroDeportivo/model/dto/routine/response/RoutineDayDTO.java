package com.utn.API_CentroDeportivo.model.dto.routine.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class RoutineDayDTO {
    private Long id;
    private Integer dayOrder;
    private String day;
    private List<ExerciseDTO> exercises;
}