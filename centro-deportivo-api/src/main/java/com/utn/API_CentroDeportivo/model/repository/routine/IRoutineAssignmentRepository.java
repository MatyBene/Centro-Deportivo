package com.utn.API_CentroDeportivo.model.repository.routine;

import com.utn.API_CentroDeportivo.model.entity.routine.RoutineAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRoutineAssignmentRepository extends JpaRepository<RoutineAssignment, Long> {
    List<RoutineAssignment> findByMemberUsername(String memberUsername);
    List<RoutineAssignment> findByMemberUsernameAndActive(String memberUsername, boolean active);
}