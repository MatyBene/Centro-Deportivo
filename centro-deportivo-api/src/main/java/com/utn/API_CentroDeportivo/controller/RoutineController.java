package com.utn.API_CentroDeportivo.controller;

import com.utn.API_CentroDeportivo.model.dto.routine.request.RoutineRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDTO;
import com.utn.API_CentroDeportivo.model.exception.UnauthorizedException;
import com.utn.API_CentroDeportivo.service.routine.IRoutineAssignmentService;
import com.utn.API_CentroDeportivo.service.routine.IRoutineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routines")
public class RoutineController {

    private final IRoutineService routineService;
    private final IRoutineAssignmentService assignmentService;

    public RoutineController(IRoutineService routineService,
                             IRoutineAssignmentService assignmentService) {
        this.routineService = routineService;
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<List<RoutineDTO>> getMyRoutines(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                routineService.findByCreatedByUsername(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoutineDTO> getRoutine(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        RoutineDTO routine = routineService.findById(id);
        if (isOwner(userDetails, routine)) {
            return ResponseEntity.ok(routine);
        }
        boolean hasAssignment = assignmentService.findByMemberUsername(userDetails.getUsername()).stream()
                .anyMatch(a -> a.getRoutineId().equals(id) && a.isActive());
        if (!hasAssignment) {
            throw new UnauthorizedException("You do not have access to this routine");
        }
        return ResponseEntity.ok(routine);
    }

    @PostMapping
    public ResponseEntity<RoutineDTO> createRoutine(
            @RequestBody RoutineRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                routineService.create(dto, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoutineDTO> updateRoutine(
            @PathVariable Long id,
            @RequestBody RoutineRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                routineService.update(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        routineService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    private boolean isOwner(UserDetails userDetails, RoutineDTO routine) {
        return routine.getCreatedBy().equals(userDetails.getUsername());
    }
}