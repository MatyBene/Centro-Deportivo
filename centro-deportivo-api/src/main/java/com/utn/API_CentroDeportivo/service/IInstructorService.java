package com.utn.API_CentroDeportivo.service;

import com.utn.API_CentroDeportivo.model.dto.response.InstructorDetailsDTO;
import com.utn.API_CentroDeportivo.model.dto.response.InstructorSummaryDTO;
import com.utn.API_CentroDeportivo.model.dto.response.UserDetailsDTO;
import com.utn.API_CentroDeportivo.model.entity.Instructor;

import java.util.Optional;

public interface IInstructorService {
    Optional<InstructorSummaryDTO> getInstructorSummaryById(long id);
    Optional<Instructor> findByUsername(String username);
    Optional<InstructorDetailsDTO> getInstructorDetailsById(Long id);
    Optional<UserDetailsDTO> findUserDetailsByUsername(String username);
}
