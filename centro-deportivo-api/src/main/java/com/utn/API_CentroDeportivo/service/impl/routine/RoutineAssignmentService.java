package com.utn.API_CentroDeportivo.service.impl.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineAssignmentDTO;
import com.utn.API_CentroDeportivo.model.entity.routine.Routine;
import com.utn.API_CentroDeportivo.model.entity.routine.RoutineAssignment;
import com.utn.API_CentroDeportivo.model.exception.RoutineNotFoundException;
import com.utn.API_CentroDeportivo.model.exception.UnauthorizedException;
import com.utn.API_CentroDeportivo.model.exception.UserNotFoundException;
import com.utn.API_CentroDeportivo.model.mapper.routine.RoutineAssignmentMapper;
import com.utn.API_CentroDeportivo.model.repository.routine.IRoutineAssignmentRepository;
import com.utn.API_CentroDeportivo.model.repository.routine.IRoutineRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.routine.IRoutineAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoutineAssignmentService implements IRoutineAssignmentService {

    private final IRoutineAssignmentRepository assignmentRepository;
    private final IRoutineRepository routineRepository;
    private final ICredentialService credentialService;

    public RoutineAssignmentService(IRoutineAssignmentRepository assignmentRepository,
                                    IRoutineRepository routineRepository,
                                    ICredentialService credentialService) {
        this.assignmentRepository = assignmentRepository;
        this.routineRepository = routineRepository;
        this.credentialService = credentialService;
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "routinesTransactionManager")
    public List<RoutineAssignmentDTO> findByMemberUsername(String username) {
        return assignmentRepository.findByMemberUsername(username)
                .stream().map(RoutineAssignmentMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "routinesTransactionManager")
    public List<RoutineAssignmentDTO> findByMemberUsernameAndActive(String username, boolean active) {
        return assignmentRepository.findByMemberUsernameAndActive(username, active)
                .stream().map(RoutineAssignmentMapper::toDTO).toList();
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public RoutineAssignmentDTO assign(Long routineId, String memberUsername,
                                       String instructorUsername) {
        if (!credentialService.existsByUsername(memberUsername)) {
            throw new UserNotFoundException("Member not found: " + memberUsername);
        }
        if (!credentialService.existsByUsername(instructorUsername)) {
            throw new UserNotFoundException("Instructor not found: " + instructorUsername);
        }
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RoutineNotFoundException(
                        "Routine not found: " + routineId));

        RoutineAssignment assignment = new RoutineAssignment();
        assignment.setRoutine(routine);
        assignment.setMemberUsername(memberUsername);
        assignment.setInstructorUsername(instructorUsername);
        assignment.setActive(true);
        assignment.setAssignedAt(LocalDate.now());

        RoutineAssignment saved = assignmentRepository.save(assignment);
        return RoutineAssignmentMapper.toDTO(saved);
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public void deactivate(Long assignmentId, String requestingUsername) {
        RoutineAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RoutineNotFoundException(
                        "Assignment not found: " + assignmentId));
        if (!assignment.getInstructorUsername().equals(requestingUsername)) {
            throw new UnauthorizedException("Only the assigning instructor can deactivate");
        }
        assignment.setActive(false);
        assignmentRepository.save(assignment);
    }
}