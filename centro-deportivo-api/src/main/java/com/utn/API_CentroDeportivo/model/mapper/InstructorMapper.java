package com.utn.API_CentroDeportivo.model.mapper;

import com.utn.API_CentroDeportivo.model.dto.request.UserRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.response.AdminViewDTO;
import com.utn.API_CentroDeportivo.model.dto.response.InstructorDetailsDTO;
import com.utn.API_CentroDeportivo.model.dto.response.InstructorSummaryDTO;
import com.utn.API_CentroDeportivo.model.dto.response.SportActivitySummaryDTO;
import com.utn.API_CentroDeportivo.model.entity.Instructor;

import java.util.List;


public class InstructorMapper {

    public static Instructor mapToInstructor(UserRequestDTO dto) {
        Instructor instructor = new Instructor();
        instructor.setName(dto.getName());
        instructor.setLastname(dto.getLastname());
        instructor.setDni(dto.getDni());
        instructor.setBirthdate(dto.getBirthdate());
        instructor.setPhone(dto.getPhone());
        instructor.setEmail(dto.getEmail());
        instructor.setSpecialty(dto.getSpecialty());
        return instructor;
    }

//    public static InstructorDetailsDTO mapToInstructorDetailsDTO(Instructor instructor){
//        InstructorDetailsDTO instructorDetailsDTO = new InstructorDetailsDTO();
//        instructorDetailsDTO.setName(instructor.getName());
//        instructorDetailsDTO.setLastname(instructor.getLastname());
//        instructorDetailsDTO.setDni(instructor.getDni());
//        instructorDetailsDTO.setBirthdate(instructor.getBirthdate());
//        instructorDetailsDTO.setPhone(instructor.getPhone());
//        instructorDetailsDTO.setEmail(instructor.getEmail());
//        instructorDetailsDTO.setSpecialty(instructor.getSpecialty());
//
//        return instructorDetailsDTO;
//    }
    public static InstructorSummaryDTO mapToInstructorSummaryDTO (Instructor instructor){
        InstructorSummaryDTO instructorSummaryDTO = new InstructorSummaryDTO();
        instructorSummaryDTO.setName(instructor.getName());
        instructorSummaryDTO.setLastname(instructor.getLastname());
        instructorSummaryDTO.setSpecialty(instructor.getSpecialty());

        return instructorSummaryDTO;

    }

    public static AdminViewDTO toAdminViewDTO(Instructor instructor) {
        AdminViewDTO dto = new AdminViewDTO();
        dto.setId(instructor.getId());
        dto.setName(instructor.getName());
        dto.setLastname(instructor.getLastname());
        dto.setUsername(instructor.getCredential().getUsername());
        dto.setRole(instructor.getCredential().getRole());
        dto.setSpecialty(instructor.getSpecialty());
        return dto;
    }

    public static InstructorDetailsDTO mapToInstructorDetailsDTO(Instructor instructor, List<SportActivitySummaryDTO> activities) {
        InstructorDetailsDTO dto = new InstructorDetailsDTO();
        dto.setName(instructor.getName());
        dto.setLastname(instructor.getLastname());
        dto.setDni(instructor.getDni());
        dto.setBirthdate(instructor.getBirthdate());
        dto.setPhone(instructor.getPhone());
        dto.setEmail(instructor.getEmail());
        dto.setUsername(instructor.getCredential().getUsername());
        dto.setRole(instructor.getCredential().getRole());
        dto.setSpecialty(instructor.getSpecialty());
        dto.setActivities(activities);
        return dto;
    }
}
