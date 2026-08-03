package com.utn.API_CentroDeportivo.model.mapper.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineAssignmentDTO;
import com.utn.API_CentroDeportivo.model.entity.routine.RoutineAssignment;

public final class RoutineAssignmentMapper {

    private RoutineAssignmentMapper() {}

    public static RoutineAssignmentDTO toDTO(RoutineAssignment entity) {
        if (entity == null) return null;
        return RoutineAssignmentDTO.builder()
                .id(entity.getId())
                .routineId(entity.getRoutine() != null ? entity.getRoutine().getId() : null)
                .routineName(entity.getRoutine() != null ? entity.getRoutine().getName() : null)
                .memberUsername(entity.getMemberUsername())
                .instructorUsername(entity.getInstructorUsername())
                .active(entity.isActive())
                .assignedAt(entity.getAssignedAt())
                .build();
    }
}
