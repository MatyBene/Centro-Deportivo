package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class RoutineAssignmentRequestDTO {
    private Long routineId;
    private String memberUsername;
}