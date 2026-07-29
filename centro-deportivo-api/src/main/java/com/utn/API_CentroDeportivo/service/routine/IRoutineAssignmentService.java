package com.utn.API_CentroDeportivo.service.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.request.RoutineAssignmentRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineAssignmentDTO;
import java.util.List;

public interface IRoutineAssignmentService {
    List<RoutineAssignmentDTO> findByMemberUsername(String username);
    List<RoutineAssignmentDTO> findByMemberUsernameAndActive(String username, boolean active);
    RoutineAssignmentDTO assign(Long routineId, String memberUsername, String instructorUsername);
    void deactivate(Long assignmentId, String requestingUsername);
}