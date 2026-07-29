package com.utn.API_CentroDeportivo.model.dto.routine.request;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class RoutineRequestDTO {
    private String name;
    private String description;
    private String level;
    private String goal;
    private Integer durationWeeks;
    private Integer daysPerWeek;
    private Boolean isTemplate;
    private WarmupRequestDTO warmup;
    private CooldownRequestDTO cooldown;
    private List<String> generalNotes;
    private List<RoutineDayRequestDTO> routineDays;
}

