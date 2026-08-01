package com.utn.API_CentroDeportivo.model.mapper.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.response.TrainingHistoryDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.TrainingSetDTO;
import com.utn.API_CentroDeportivo.model.entity.routine.TrainingHistory;
import com.utn.API_CentroDeportivo.model.entity.routine.TrainingSet;
import java.util.*;

public final class TrainingHistoryMapper {

    private TrainingHistoryMapper() {}

    public static TrainingHistoryDTO toDTO(TrainingHistory entity) {
        if (entity == null) return null;
        return TrainingHistoryDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .date(entity.getDate())
                .routineId(entity.getRoutine() != null ? entity.getRoutine().getId() : null)
                .exerciseId(entity.getExercise() != null ? entity.getExercise().getId() : null)
                .sets(toTrainingSetDTOList(entity.getSets()))
                .notes(entity.getNotes())
                .build();
    }

    private static TrainingSetDTO toTrainingSetDTO(TrainingSet entity) {
        if (entity == null) return null;
        return TrainingSetDTO.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .weight(entity.getWeight())
                .repetitions(entity.getRepetitions())
                .build();
    }

    private static List<TrainingSetDTO> toTrainingSetDTOList(List<TrainingSet> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(TrainingHistoryMapper::toTrainingSetDTO).toList();
    }
}