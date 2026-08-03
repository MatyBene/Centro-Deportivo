package com.utn.API_CentroDeportivo.controller;

import com.utn.API_CentroDeportivo.model.dto.routine.request.RoutineAssignmentRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineAssignmentDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDTO;
import com.utn.API_CentroDeportivo.model.exception.UnauthorizedException;
import com.utn.API_CentroDeportivo.service.routine.IRoutineAssignmentService;
import com.utn.API_CentroDeportivo.service.routine.IRoutineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routineAssignments")
public class RoutineAssignmentController {

    private final IRoutineAssignmentService assignmentService;
    private final IRoutineService routineService;

    public RoutineAssignmentController(IRoutineAssignmentService assignmentService,
                                       IRoutineService routineService) {
        this.assignmentService = assignmentService;
        this.routineService = routineService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<RoutineAssignmentDTO>> getMyAssignments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                assignmentService.findByMemberUsername(userDetails.getUsername()));
    }

    @GetMapping("/me/active")
    public ResponseEntity<List<RoutineAssignmentDTO>> getMyActiveAssignments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                assignmentService.findByMemberUsernameAndActive(
                        userDetails.getUsername(), true));
    }

    @GetMapping
    public ResponseEntity<List<RoutineAssignmentDTO>> getAssignments(
            @RequestParam(required = false) String memberUsername,
            @RequestParam(defaultValue = "true") boolean active,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                assignmentService.findByMemberUsernameAndActive(userDetails.getUsername(), active));
    }

    @PostMapping
    public ResponseEntity<RoutineAssignmentDTO> assign(
            @RequestBody RoutineAssignmentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        RoutineDTO routine = routineService.findById(dto.getRoutineId());
        if (!routine.getCreatedBy().equals(userDetails.getUsername())) {
            throw new UnauthorizedException("Only the routine creator can assign it");
        }
        return ResponseEntity.ok(
                assignmentService.assign(dto.getRoutineId(),
                        dto.getMemberUsername(), userDetails.getUsername()));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        assignmentService.deactivate(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}