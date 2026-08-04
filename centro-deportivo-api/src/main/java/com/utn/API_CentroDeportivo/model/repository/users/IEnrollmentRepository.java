package com.utn.API_CentroDeportivo.model.repository.users;

import com.utn.API_CentroDeportivo.model.entity.users.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IEnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByMemberIdAndActivityId(Long memberId, Long activityId);
    boolean existsByMemberId(Long memberId);
    List<Enrollment> findByMemberId(Long memberId);

    @Query("SELECT e FROM Enrollment e WHERE e.endDate < :cutoff")
    List<Enrollment> findExpired(@Param("cutoff") LocalDate cutoff);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.member.id = :memberId AND e.endDate >= :cutoff")
    boolean existsActiveByMemberId(@Param("memberId") Long memberId, @Param("cutoff") LocalDate cutoff);
}
