package com.utn.API_CentroDeportivo.model.repository.routine;

import com.utn.API_CentroDeportivo.model.entity.routine.TrainingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITrainingHistoryRepository extends JpaRepository<TrainingHistory, Long> {
    List<TrainingHistory> findByUsername(String username);
}