package com.utn.API_CentroDeportivo.model.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class EnrollmentDTO {
    private String activityName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long activityId;
}
