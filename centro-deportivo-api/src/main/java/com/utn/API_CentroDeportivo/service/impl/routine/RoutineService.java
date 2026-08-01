package com.utn.API_CentroDeportivo.service.impl.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.request.ExerciseRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.request.RoutineDayRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.request.RoutineRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.request.SeriesRepetitionRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDTO;
import com.utn.API_CentroDeportivo.model.entity.routine.Exercise;
import com.utn.API_CentroDeportivo.model.entity.routine.Routine;
import com.utn.API_CentroDeportivo.model.entity.routine.RoutineDay;
import com.utn.API_CentroDeportivo.model.entity.routine.TrainingHistory;
import com.utn.API_CentroDeportivo.model.exception.RoutineNotFoundException;
import com.utn.API_CentroDeportivo.model.exception.UnauthorizedException;
import com.utn.API_CentroDeportivo.model.exception.UserNotFoundException;
import com.utn.API_CentroDeportivo.model.mapper.routine.RoutineMapper;
import com.utn.API_CentroDeportivo.model.repository.routine.IRoutineRepository;
import com.utn.API_CentroDeportivo.model.repository.routine.ITrainingHistoryRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.routine.IRoutineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RoutineService implements IRoutineService {

    private final IRoutineRepository routineRepository;
    private final ITrainingHistoryRepository trainingHistoryRepository;
    private final ICredentialService credentialService;

    public RoutineService(IRoutineRepository routineRepository,
                          ITrainingHistoryRepository trainingHistoryRepository,
                          ICredentialService credentialService) {
        this.routineRepository = routineRepository;
        this.trainingHistoryRepository = trainingHistoryRepository;
        this.credentialService = credentialService;
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "routinesTransactionManager")
    public List<RoutineDTO> findByCreatedByUsername(String username) {
        return routineRepository.findByCreatedByUsername(username)
                .stream().map(RoutineMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "routinesTransactionManager")
    public RoutineDTO findById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException("Routine not found: " + id));
        return RoutineMapper.toDTO(routine);
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public RoutineDTO create(RoutineRequestDTO dto, String createdByUsername) {
        if (!credentialService.existsByUsername(createdByUsername)) {
            throw new UserNotFoundException("User not found: " + createdByUsername);
        }
        Routine entity = RoutineMapper.toEntity(dto, createdByUsername);
        Routine saved = routineRepository.save(entity);
        return RoutineMapper.toDTO(saved);
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public RoutineDTO update(Long id, RoutineRequestDTO dto, String ownerUsername) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException("Routine not found: " + id));
        if (!routine.getCreatedByUsername().equals(ownerUsername)) {
            throw new UnauthorizedException("User " + ownerUsername
                    + " does not own this routine");
        }

        routine.setName(dto.getName());
        routine.setDescription(dto.getDescription());
        routine.setLevel(dto.getLevel());
        routine.setGoal(dto.getGoal());
        routine.setDurationWeeks(dto.getDurationWeeks());
        routine.setDaysPerWeek(dto.getDaysPerWeek());
        routine.setTemplate(dto.getIsTemplate());
        routine.setWarmup(RoutineMapper.toWarmupEntity(dto.getWarmup()));
        routine.setCooldown(RoutineMapper.toCooldownEntity(dto.getCooldown()));
        routine.setGeneralNotes(dto.getGeneralNotes() == null ? new ArrayList<>() : new ArrayList<>(dto.getGeneralNotes()));

        updateRoutineDays(routine, dto.getRoutineDays());

        Routine saved = routineRepository.save(routine);
        return RoutineMapper.toDTO(saved);
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public void delete(Long id, String ownerUsername) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException("Routine not found: " + id));
        if (!routine.getCreatedByUsername().equals(ownerUsername)) {
            throw new UnauthorizedException("User " + ownerUsername
                    + " does not own this routine");
        }
        List<TrainingHistory> histories = trainingHistoryRepository.findByRoutineId(id);
        trainingHistoryRepository.deleteAll(histories);
        routineRepository.delete(routine);
    }

    private void updateRoutineDays(Routine routine, List<RoutineDayRequestDTO> dayDtos) {
        if (dayDtos == null) {
            return;
        }

        List<RoutineDay> existingDays = new ArrayList<>(routine.getRoutineDays());
        List<RoutineDay> daysToKeep = new ArrayList<>();

        for (RoutineDayRequestDTO dayDto : dayDtos) {
            RoutineDay day = findOrCreateDay(existingDays, dayDto);
            day.setDayOrder(dayDto.getDayNumber());
            day.setDay(dayDto.getName());
            day.setDescription(dayDto.getDescription());
            day.setOrder(dayDto.getOrder());
            day.setRoutine(routine);

            updateExercises(day, dayDto.getExercises());

            if (!routine.getRoutineDays().contains(day)) {
                routine.getRoutineDays().add(day);
            }
            daysToKeep.add(day);
        }

        List<RoutineDay> daysToRemove = existingDays.stream()
                .filter(d -> !daysToKeep.contains(d))
                .toList();

        for (RoutineDay day : daysToRemove) {
            deleteTrainingHistoryForDay(day);
            routine.getRoutineDays().remove(day);
        }
    }

    private RoutineDay findOrCreateDay(List<RoutineDay> existingDays, RoutineDayRequestDTO dto) {
        if (dto.getId() == null) {
            return new RoutineDay();
        }
        return existingDays.stream()
                .filter(d -> dto.getId().equals(d.getId()))
                .findFirst()
                .orElseGet(RoutineDay::new);
    }

    private void updateExercises(RoutineDay day, List<ExerciseRequestDTO> exerciseDtos) {
        if (exerciseDtos == null) {
            return;
        }

        List<Exercise> existingExercises = new ArrayList<>(day.getExercises());
        List<Exercise> exercisesToKeep = new ArrayList<>();

        for (ExerciseRequestDTO exDto : exerciseDtos) {
            Exercise exercise = findOrCreateExercise(existingExercises, exDto);
            exercise.setName(exDto.getName());
            exercise.setMuscleGroup(exDto.getMuscleGroup());
            exercise.setType(exDto.getType());
            exercise.setNotes(exDto.getNotes());
            exercise.setSuggestedWeight(exDto.getSuggestedWeight());
            exercise.setRestSeconds(exDto.getRestSeconds());
            exercise.setExerciseOrder(exDto.getExerciseOrder());
            exercise.setSeriesRepetitions(toSeriesRepetitions(exDto.getSeriesRepetitions()));
            exercise.setRoutineDay(day);

            if (!day.getExercises().contains(exercise)) {
                day.getExercises().add(exercise);
            }
            exercisesToKeep.add(exercise);
        }

        List<Exercise> exercisesToRemove = existingExercises.stream()
                .filter(e -> !exercisesToKeep.contains(e))
                .toList();

        for (Exercise exercise : exercisesToRemove) {
            deleteTrainingHistoryForExercise(exercise);
            day.getExercises().remove(exercise);
        }
    }

    private Exercise findOrCreateExercise(List<Exercise> existingExercises, ExerciseRequestDTO dto) {
        if (dto.getId() == null) {
            return new Exercise();
        }
        return existingExercises.stream()
                .filter(e -> dto.getId().equals(e.getId()))
                .findFirst()
                .orElseGet(Exercise::new);
    }

    private List<String> toSeriesRepetitions(List<SeriesRepetitionRequestDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(dtos.stream()
                .map(SeriesRepetitionRequestDTO::getRepetitions)
                .toList());
    }

    private void deleteTrainingHistoryForDay(RoutineDay day) {
        if (day.getExercises() == null) {
            return;
        }
        day.getExercises().forEach(this::deleteTrainingHistoryForExercise);
    }

    private void deleteTrainingHistoryForExercise(Exercise exercise) {
        if (exercise.getId() == null) {
            return;
        }
        List<TrainingHistory> histories = trainingHistoryRepository.findByExerciseId(exercise.getId());
        if (!histories.isEmpty()) {
            trainingHistoryRepository.deleteAll(histories);
        }
    }
}