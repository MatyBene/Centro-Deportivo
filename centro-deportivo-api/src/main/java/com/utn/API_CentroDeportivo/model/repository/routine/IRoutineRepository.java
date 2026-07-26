package com.utn.API_CentroDeportivo.model.repository.routine;

import com.utn.API_CentroDeportivo.model.entity.routine.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByCreatedByUsername(String username);
}