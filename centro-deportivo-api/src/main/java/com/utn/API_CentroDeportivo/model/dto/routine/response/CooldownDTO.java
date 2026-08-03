package com.utn.API_CentroDeportivo.model.dto.routine.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class CooldownDTO {
    private Integer durationMinutes;
    private List<String> activities;
}