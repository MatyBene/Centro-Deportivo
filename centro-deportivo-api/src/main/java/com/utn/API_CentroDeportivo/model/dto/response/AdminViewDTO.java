package com.utn.API_CentroDeportivo.model.dto.response;

import com.utn.API_CentroDeportivo.model.enums.PermissionLevel;
import com.utn.API_CentroDeportivo.model.enums.Role;
import com.utn.API_CentroDeportivo.model.enums.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class AdminViewDTO {
    private Long id;
    private String name;
    private String lastname;
    private String username;
    private Role role;
    private Status status;
    private String specialty;
    private PermissionLevel permissionLevel;
}
