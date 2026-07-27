package com.utn.API_CentroDeportivo.model.dto.routine.response;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class RoutineAssignmentDTO {
    private Long id;
    private Long routineId;
    private String routineName;
    private String memberUsername;
    private String instructorUsername;
    private boolean active;
    private LocalDate assignedAt;
}