package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class TrainingHistoryRequestDTO {
    private Long routineId;
    private Long exerciseId;
    private LocalDate date;
    private List<TrainingSetRequestDTO> sets;
    private String notes;
}