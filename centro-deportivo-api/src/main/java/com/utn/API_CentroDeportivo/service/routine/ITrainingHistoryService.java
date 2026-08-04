package com.utn.API_CentroDeportivo.service.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.request.TrainingHistoryRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.TrainingHistoryDTO;
import java.util.List;

public interface ITrainingHistoryService {
    List<TrainingHistoryDTO> findByUsername(String username);
    TrainingHistoryDTO save(TrainingHistoryRequestDTO dto, String username);
}