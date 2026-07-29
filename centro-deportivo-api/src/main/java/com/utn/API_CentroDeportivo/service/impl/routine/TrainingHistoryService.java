package com.utn.API_CentroDeportivo.service.impl.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.request.TrainingHistoryRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.TrainingHistoryDTO;
import com.utn.API_CentroDeportivo.model.entity.routine.Exercise;
import com.utn.API_CentroDeportivo.model.entity.routine.Routine;
import com.utn.API_CentroDeportivo.model.entity.routine.TrainingHistory;
import com.utn.API_CentroDeportivo.model.entity.routine.TrainingSet;
import com.utn.API_CentroDeportivo.model.exception.RoutineNotFoundException;
import com.utn.API_CentroDeportivo.model.exception.UserNotFoundException;
import com.utn.API_CentroDeportivo.model.mapper.routine.TrainingHistoryMapper;
import com.utn.API_CentroDeportivo.model.repository.routine.IExerciseRepository;
import com.utn.API_CentroDeportivo.model.repository.routine.IRoutineRepository;
import com.utn.API_CentroDeportivo.model.repository.routine.ITrainingHistoryRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.routine.ITrainingHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingHistoryService implements ITrainingHistoryService {

    private final ITrainingHistoryRepository trainingHistoryRepository;
    private final IRoutineRepository routineRepository;
    private final IExerciseRepository exerciseRepository;
    private final ICredentialService credentialService;

    public TrainingHistoryService(ITrainingHistoryRepository trainingHistoryRepository,
                                  IRoutineRepository routineRepository,
                                  IExerciseRepository exerciseRepository,
                                  ICredentialService credentialService) {
        this.trainingHistoryRepository = trainingHistoryRepository;
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.credentialService = credentialService;
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "routinesTransactionManager")
    public List<TrainingHistoryDTO> findByUsername(String username) {
        return trainingHistoryRepository.findByUsername(username)
                .stream().map(TrainingHistoryMapper::toDTO).toList();
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public TrainingHistoryDTO save(TrainingHistoryRequestDTO dto, String username) {
        if (!credentialService.existsByUsername(username)) {
            throw new UserNotFoundException("User not found: " + username);
        }
        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new RoutineNotFoundException(
                        "Routine not found: " + dto.getRoutineId()));
        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new RoutineNotFoundException(
                        "Exercise not found: " + dto.getExerciseId()));

        TrainingHistory history = new TrainingHistory();
        history.setUsername(username);
        history.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
        history.setRoutine(routine);
        history.setExercise(exercise);

        List<TrainingSet> sets = new ArrayList<>();
        if (dto.getSets() != null) {
            for (var setDto : dto.getSets()) {
                TrainingSet set = new TrainingSet();
                set.setNumber(setDto.getNumber());
                set.setWeight(setDto.getWeight());
                set.setRepetitions(setDto.getRepetitions());
                set.setTrainingHistory(history);  // ← back-reference
                sets.add(set);
            }
        }
        history.setSets(sets);

        TrainingHistory saved = trainingHistoryRepository.save(history);
        return TrainingHistoryMapper.toDTO(saved);
    }
}