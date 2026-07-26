package com.utn.API_CentroDeportivo.service;

import com.utn.API_CentroDeportivo.model.dto.response.SportActivityDetailsDTO;
import com.utn.API_CentroDeportivo.model.dto.response.SportActivitySummaryDTO;
import com.utn.API_CentroDeportivo.model.entity.users.Instructor;
import com.utn.API_CentroDeportivo.model.entity.users.SportActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ISportActivityService {
    Page<SportActivitySummaryDTO> getActivities(Pageable pageable);
    Page<SportActivitySummaryDTO> findActivitiesByName(String name, Pageable pageable);
    Page<SportActivitySummaryDTO> findActivitiesByTimeRange(String startTime, String endTime, Pageable pageable);
    Optional<SportActivityDetailsDTO> getActivityById(Long id);
    int getCurrentMembers(Long id);
    List<SportActivitySummaryDTO> getActivitiesByInstructor(Instructor instructor);
    List<SportActivityDetailsDTO> getActivitiesDetailsByInstructor(Instructor instructor);
    Optional<SportActivity> getSportActivityEntityById(Long id);
}
