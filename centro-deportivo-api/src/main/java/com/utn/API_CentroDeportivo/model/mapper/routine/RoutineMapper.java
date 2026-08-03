package com.utn.API_CentroDeportivo.model.mapper.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.request.*;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDayDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.ExerciseDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.SeriesRepetitionDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.WarmupDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.CooldownDTO;
import com.utn.API_CentroDeportivo.model.entity.routine.Routine;
import com.utn.API_CentroDeportivo.model.entity.routine.RoutineDay;
import com.utn.API_CentroDeportivo.model.entity.routine.Exercise;
import com.utn.API_CentroDeportivo.model.entity.routine.Warmup;
import com.utn.API_CentroDeportivo.model.entity.routine.Cooldown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RoutineMapper {

    private RoutineMapper() {}

    public static RoutineDTO toDTO(Routine entity) {
        if (entity == null) return null;
        return RoutineDTO.builder()
                .id(entity.getId())
                .createdBy(entity.getCreatedByUsername())
                .name(entity.getName())
                .description(entity.getDescription())
                .level(entity.getLevel())
                .goal(entity.getGoal())
                .durationWeeks(entity.getDurationWeeks())
                .daysPerWeek(entity.getDaysPerWeek())
                .createdAt(entity.getCreatedAt())
                .isTemplate(entity.isTemplate())
                .warmup(toWarmupDTO(entity.getWarmup()))
                .cooldown(toCooldownDTO(entity.getCooldown()))
                .generalNotes(entity.getGeneralNotes() == null ? null : new ArrayList<>(entity.getGeneralNotes()))
                .routineDays(toRoutineDayDTOList(entity.getRoutineDays()))
                .build();
    }

    public static Routine toEntity(RoutineRequestDTO dto, String createdByUsername) {
        if (dto == null) return null;
        Routine entity = new Routine();
        entity.setCreatedByUsername(createdByUsername);
        applyRequestToEntity(dto, entity);
        return entity;
    }

    public static void updateEntity(Routine entity, RoutineRequestDTO dto) {
        if (dto == null || entity == null) return;
        applyRequestToEntity(dto, entity);
    }

    private static void applyRequestToEntity(RoutineRequestDTO dto, Routine entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setLevel(dto.getLevel());
        entity.setGoal(dto.getGoal());
        entity.setDurationWeeks(dto.getDurationWeeks());
        entity.setDaysPerWeek(dto.getDaysPerWeek());
        entity.setWarmup(toWarmupEntity(dto.getWarmup()));
        entity.setCooldown(toCooldownEntity(dto.getCooldown()));
        entity.setGeneralNotes(dto.getGeneralNotes() == null ? new ArrayList<>() : new ArrayList<>(dto.getGeneralNotes()));
        if (dto.getRoutineDays() != null) {
            java.util.List<RoutineDay> days = dto.getRoutineDays().stream()
                    .map(RoutineMapper::toRoutineDayEntity).toList();
            days.forEach(day -> day.setRoutine(entity));
            entity.setRoutineDays(new ArrayList<>(days));
        }
    }

    public static RoutineDayDTO toRoutineDayDTO(RoutineDay entity) {
        if (entity == null) return null;
        return RoutineDayDTO.builder()
                .id(entity.getId())
                .dayNumber(entity.getDayOrder())
                .name(entity.getDay())
                .description(entity.getDescription())
                .order(entity.getOrder())
                .exercises(toExerciseDTOList(entity.getExercises()))
                .build();
    }

    public static RoutineDay toRoutineDayEntity(RoutineDayRequestDTO dto) {
        if (dto == null) return null;
        RoutineDay entity = new RoutineDay();
        entity.setDayOrder(dto.getDayNumber());
        entity.setDay(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setOrder(dto.getOrder());
        if (dto.getExercises() != null) {
            List<Exercise> exercises = new ArrayList<>(dto.getExercises().stream()
                    .map(RoutineMapper::toExerciseEntity).toList());
            exercises.forEach(e -> e.setRoutineDay(entity));
            entity.setExercises(exercises);
        }
        return entity;
    }

    public static ExerciseDTO toExerciseDTO(Exercise entity) {
        if (entity == null) return null;
        return ExerciseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .muscleGroup(entity.getMuscleGroup())
                .type(entity.getType())
                .restSeconds(entity.getRestSeconds())
                .notes(entity.getNotes())
                .suggestedWeight(entity.getSuggestedWeight())
                .exerciseOrder(entity.getExerciseOrder())
                .seriesRepetitions(toSeriesRepetitionDTOList(entity.getSeriesRepetitions()))
                .build();
    }

    private static Exercise toExerciseEntity(ExerciseRequestDTO dto) {
        if (dto == null) return null;
        Exercise entity = new Exercise();
        entity.setName(dto.getName());
        entity.setMuscleGroup(dto.getMuscleGroup());
        entity.setType(dto.getType());
        entity.setRestSeconds(dto.getRestSeconds());
        entity.setNotes(dto.getNotes());
        entity.setSuggestedWeight(dto.getSuggestedWeight());
        entity.setExerciseOrder(dto.getExerciseOrder());
        entity.setSeriesRepetitions(toSeriesRepetitionEntityList(dto.getSeriesRepetitions()));
        return entity;
    }

    public static SeriesRepetitionDTO toSeriesRepetitionDTO(String entityValue) {
        if (entityValue == null) return null;
        return SeriesRepetitionDTO.builder().repetitions(entityValue).build();
    }

    private static String toSeriesRepetitionEntity(SeriesRepetitionRequestDTO dto) {
        return dto == null ? null : dto.getRepetitions();
    }

    public static WarmupDTO toWarmupDTO(Warmup w) {
        if (w == null) return null;
        return WarmupDTO.builder()
                .durationMinutes(w.getDurationMinutes())
                .activities(w.getActivities() == null ? null : new ArrayList<>(w.getActivities()))
                .build();
    }

    public static Warmup toWarmupEntity(WarmupRequestDTO dto) {
        if (dto == null) return null;
        Warmup w = new Warmup();
        w.setDurationMinutes(dto.getDurationMinutes());
        w.setActivities(dto.getActivities() == null ? new ArrayList<>() : new ArrayList<>(dto.getActivities()));
        return w;
    }

    public static CooldownDTO toCooldownDTO(Cooldown c) {
        if (c == null) return null;
        return CooldownDTO.builder()
                .durationMinutes(c.getDurationMinutes())
                .activities(c.getActivities() == null ? null : new ArrayList<>(c.getActivities()))
                .build();
    }

    public static Cooldown toCooldownEntity(CooldownRequestDTO dto) {
        if (dto == null) return null;
        Cooldown c = new Cooldown();
        c.setDurationMinutes(dto.getDurationMinutes());
        c.setActivities(dto.getActivities() == null ? new ArrayList<>() : new ArrayList<>(dto.getActivities()));
        return c;
    }

    private static List<RoutineDayDTO> toRoutineDayDTOList(List<RoutineDay> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(RoutineMapper::toRoutineDayDTO).toList();
    }

    private static List<ExerciseDTO> toExerciseDTOList(List<Exercise> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(RoutineMapper::toExerciseDTO).toList();
    }

    private static List<SeriesRepetitionDTO> toSeriesRepetitionDTOList(List<String> values) {
        if (values == null) return Collections.emptyList();
        return values.stream().map(RoutineMapper::toSeriesRepetitionDTO).toList();
    }

    private static List<String> toSeriesRepetitionEntityList(
            List<SeriesRepetitionRequestDTO> dtos) {

        if (dtos == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(dtos.stream()
                .map(RoutineMapper::toSeriesRepetitionEntity)
                .toList());
    }
}