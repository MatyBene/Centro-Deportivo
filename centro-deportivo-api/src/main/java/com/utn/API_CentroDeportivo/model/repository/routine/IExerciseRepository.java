package com.utn.API_CentroDeportivo.model.repository.routine;

import com.utn.API_CentroDeportivo.model.entity.routine.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IExerciseRepository extends JpaRepository<Exercise, Long> {
}