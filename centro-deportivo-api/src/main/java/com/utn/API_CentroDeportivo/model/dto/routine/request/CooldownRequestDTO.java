package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class CooldownRequestDTO {
    private Integer durationMinutes;
    private List<String> activities;
}
