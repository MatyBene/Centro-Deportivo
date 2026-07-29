package com.utn.API_CentroDeportivo.service.impl.routine;

import com.utn.API_CentroDeportivo.model.dto.routine.request.RoutineRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.routine.response.RoutineDTO;
import com.utn.API_CentroDeportivo.model.entity.routine.Routine;
import com.utn.API_CentroDeportivo.model.exception.RoutineNotFoundException;
import com.utn.API_CentroDeportivo.model.exception.UnauthorizedException;
import com.utn.API_CentroDeportivo.model.exception.UserNotFoundException;
import com.utn.API_CentroDeportivo.model.mapper.routine.RoutineMapper;
import com.utn.API_CentroDeportivo.model.repository.routine.IRoutineRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.routine.IRoutineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoutineService implements IRoutineService {

    private final IRoutineRepository routineRepository;
    private final ICredentialService credentialService;

    public RoutineService(IRoutineRepository routineRepository,
                          ICredentialService credentialService) {
        this.routineRepository = routineRepository;
        this.credentialService = credentialService;
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "routinesTransactionManager")
    public List<RoutineDTO> findByCreatedByUsername(String username) {
        return routineRepository.findByCreatedByUsername(username)
                .stream().map(RoutineMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "routinesTransactionManager")
    public RoutineDTO findById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException("Routine not found: " + id));
        return RoutineMapper.toDTO(routine);
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public RoutineDTO create(RoutineRequestDTO dto, String createdByUsername) {
        if (!credentialService.existsByUsername(createdByUsername)) {
            throw new UserNotFoundException("User not found: " + createdByUsername);
        }
        Routine entity = RoutineMapper.toEntity(dto, createdByUsername);
        Routine saved = routineRepository.save(entity);
        return RoutineMapper.toDTO(saved);
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public RoutineDTO update(Long id, RoutineRequestDTO dto, String ownerUsername) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException("Routine not found: " + id));
        if (!routine.getCreatedByUsername().equals(ownerUsername)) {
            throw new UnauthorizedException("User " + ownerUsername
                    + " does not own this routine");
        }
        RoutineMapper.updateEntity(routine, dto);
        Routine saved = routineRepository.save(routine);
        return RoutineMapper.toDTO(saved);
    }

    @Override
    @Transactional(transactionManager = "routinesTransactionManager")
    public void delete(Long id, String ownerUsername) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException("Routine not found: " + id));
        if (!routine.getCreatedByUsername().equals(ownerUsername)) {
            throw new UnauthorizedException("User " + ownerUsername
                    + " does not own this routine");
        }
        routineRepository.delete(routine);
    }
}