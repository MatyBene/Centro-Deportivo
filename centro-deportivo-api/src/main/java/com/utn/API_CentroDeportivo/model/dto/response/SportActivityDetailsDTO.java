package com.utn.API_CentroDeportivo.model.dto.response;

import com.utn.API_CentroDeportivo.model.enums.Day;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class SportActivityDetailsDTO extends SportActivitySummaryDTO{
    private String description;
    private int currentMembers;
    private String startTime;
    private String endTime;
    private List<Day> classDays;
}
