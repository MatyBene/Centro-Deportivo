package com.utn.API_CentroDeportivo.model.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString

public class UserSummaryDTO {
    private Long id;
    private String name;
    private String lastname;
}
