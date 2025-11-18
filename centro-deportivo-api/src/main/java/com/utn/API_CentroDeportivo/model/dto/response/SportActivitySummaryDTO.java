package com.utn.API_CentroDeportivo.model.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString

public class SportActivitySummaryDTO {
    private Long id;
    private String name;
    private int maxMembers;
    private Long instructorId;
    private String instructorName;
}
