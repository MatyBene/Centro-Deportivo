package com.utn.API_CentroDeportivo.model.dto.routine.response;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class TrainingHistoryDTO {
    private Long id;
    private String username;
    private LocalDate date;
    private Long routineId;
    private Long exerciseId;
    private List<TrainingSetDTO> sets;
}