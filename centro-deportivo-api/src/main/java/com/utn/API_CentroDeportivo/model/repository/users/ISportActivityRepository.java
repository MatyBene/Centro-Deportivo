package com.utn.API_CentroDeportivo.model.repository.users;

import com.utn.API_CentroDeportivo.model.entity.users.Instructor;
import com.utn.API_CentroDeportivo.model.entity.users.SportActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface ISportActivityRepository extends JpaRepository<SportActivity, Long> {
    List<SportActivity> findByInstructor(Instructor instructor);
    Page<SportActivity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT a FROM SportActivity a WHERE a.startTime < :endTimeTo AND a.endTime > :startTimeFrom")
    Page<SportActivity> findByTimeRangeOverlap(@Param("startTimeFrom") LocalTime startTimeFrom,
                                               @Param("endTimeTo") LocalTime endTimeTo,
                                               Pageable pageable);
}
