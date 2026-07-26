package com.utn.API_CentroDeportivo.model.repository.users;

import com.utn.API_CentroDeportivo.model.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IEnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByMemberIdAndActivityId(Long memberId, Long activityId);
    boolean existsByMemberId(Long memberId);
    List<Enrollment> findByMemberId(Long memberId);
}
