package com.utn.API_CentroDeportivo.model.mapper;

import com.utn.API_CentroDeportivo.model.dto.request.UserRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.response.AdminDetailsDTO;
import com.utn.API_CentroDeportivo.model.dto.response.AdminViewDTO;
import com.utn.API_CentroDeportivo.model.entity.users.Admin;

import java.time.LocalDate;

public class AdminMapper {

    public static Admin mapToAdmin(UserRequestDTO dto) {
        Admin admin = new Admin();
        admin.setName(dto.getName());
        admin.setLastname(dto.getLastname());
        admin.setDni(dto.getDni());
        admin.setBirthdate(dto.getBirthdate());
        admin.setPhone(dto.getPhone());
        admin.setEmail(dto.getEmail());
        admin.setPermissionLevel(dto.getPermissionLevel());
        admin.setHireDate(LocalDate.now());
        return admin;
    }

    public static AdminViewDTO toAdminViewDTO(Admin admin) {
        AdminViewDTO dto = new AdminViewDTO();
        dto.setId(admin.getId());
        dto.setName(admin.getName());
        dto.setLastname(admin.getLastname());
        dto.setUsername(admin.getCredential().getUsername());
        dto.setRole(admin.getCredential().getRole());
        dto.setPermissionLevel(admin.getPermissionLevel());
        return dto;
    }

    public static AdminDetailsDTO mapToAdminDetailsDTO(Admin admin) {
        return AdminDetailsDTO.builder()
                .name(admin.getName())
                .lastname(admin.getLastname())
                .dni(admin.getDni())
                .birthdate(admin.getBirthdate())
                .phone(admin.getPhone())
                .email(admin.getEmail())
                .username(admin.getCredential().getUsername())
                .role(admin.getCredential().getRole())
                .hireDate(admin.getHireDate())
                .permissionLevel(admin.getPermissionLevel())
                .build();
    }
}
