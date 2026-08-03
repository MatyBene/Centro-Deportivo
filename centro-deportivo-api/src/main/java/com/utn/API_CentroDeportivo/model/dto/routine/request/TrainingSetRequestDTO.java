package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class TrainingSetRequestDTO {
    private Integer number;
    private Integer weight;
    private Integer repetitions;
}
