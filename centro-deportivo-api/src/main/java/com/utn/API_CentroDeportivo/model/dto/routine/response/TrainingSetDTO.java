package com.utn.API_CentroDeportivo.model.dto.routine.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class TrainingSetDTO {
    private Long id;
    private Integer number;
    private Integer weight;
    private Integer repetitions;
}