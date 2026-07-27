package com.utn.API_CentroDeportivo.model.dto.routine.response;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class RoutineDTO {
    private Long id;
    private String createdBy;
    private String name;
    private String description;
    private String level;
    private String goal;
    private Integer durationWeeks;
    private Integer daysPerWeek;
    private LocalDate createdAt;
    private boolean isTemplate;
    private WarmupDTO warmup;
    private CooldownDTO cooldown;
    private List<String> generalNotes;
    private List<RoutineDayDTO> routineDays;
}