package com.utn.API_CentroDeportivo.controller;

import com.utn.API_CentroDeportivo.model.dto.routine.request.TrainingHistoryRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.ExerciseDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineAssignmentDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.TrainingHistoryDTO;
import com.utn.API_CentroDeportivo.model.exception.UnauthorizedException;
import com.utn.API_CentroDeportivo.service.routine.IRoutineAssignmentService;
import com.utn.API_CentroDeportivo.service.routine.IRoutineService;
import com.utn.API_CentroDeportivo.service.routine.ITrainingHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainingHistory")
public class TrainingHistoryController {

    private final ITrainingHistoryService trainingHistoryService;
    private final IRoutineAssignmentService assignmentService;
    private final IRoutineService routineService;

    public TrainingHistoryController(ITrainingHistoryService trainingHistoryService,
                                     IRoutineAssignmentService assignmentService,
                                     IRoutineService routineService) {
        this.trainingHistoryService = trainingHistoryService;
        this.assignmentService = assignmentService;
        this.routineService = routineService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingHistoryDTO>> getMyHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                trainingHistoryService.findByUsername(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<TrainingHistoryDTO> saveHistory(
            @RequestBody TrainingHistoryRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        validateRoutineAccess(username, dto.getRoutineId());
        validateExerciseBelongsToRoutine(dto.getRoutineId(), dto.getExerciseId());
        return ResponseEntity.ok(
                trainingHistoryService.save(dto, username));
    }

    private void validateRoutineAccess(String username, Long routineId) {
        RoutineDTO routine = routineService.findById(routineId);
        boolean isOwner = routine.getCreatedBy().equals(username);
        boolean assigned = assignmentService.findByMemberUsernameAndActive(username, true).stream()
                .anyMatch(a -> a.getRoutineId().equals(routineId));
        if (!isOwner && !assigned) {
            throw new UnauthorizedException("User does not have access to this routine");
        }
    }

    private void validateExerciseBelongsToRoutine(Long routineId, Long exerciseId) {
        RoutineDTO routine = routineService.findById(routineId);
        boolean found = routine.getRoutineDays().stream()
                .flatMap(day -> day.getExercises().stream())
                .anyMatch(exercise -> exercise.getId().equals(exerciseId));
        if (!found) {
            throw new UnauthorizedException("Exercise does not belong to the routine");
        }
    }
}