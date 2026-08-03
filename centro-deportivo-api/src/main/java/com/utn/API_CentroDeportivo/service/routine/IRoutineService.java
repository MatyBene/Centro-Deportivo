package com.utn.API_CentroDeportivo.service.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.request.RoutineRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDTO;
import java.util.List;

public interface IRoutineService {
    List<RoutineDTO> findByCreatedByUsername(String username);
    RoutineDTO findById(Long id);
    RoutineDTO create(RoutineRequestDTO dto, String createdByUsername);
    RoutineDTO update(Long id, RoutineRequestDTO dto, String ownerUsername);
    void delete(Long id, String ownerUsername);
}