package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.entity.users.Enrollment;
import com.utn.API_CentroDeportivo.model.exception.MemberNotFoundException;
import com.utn.API_CentroDeportivo.model.repository.users.IEnrollmentRepository;
import com.utn.API_CentroDeportivo.service.IEnrollmentExpirationService;
import com.utn.API_CentroDeportivo.service.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnrollmentExpirationService implements IEnrollmentExpirationService {

    private final IEnrollmentRepository enrollmentRepository;
    private final IMemberService memberService;
    private final Clock clock;

    public EnrollmentExpirationService(IEnrollmentRepository enrollmentRepository,
                                       IMemberService memberService, Clock clock) {
        this.enrollmentRepository = enrollmentRepository;
        this.memberService = memberService;
        this.clock = clock;
    }

    @Scheduled(cron = "${enrollment.expiration.cron:0 0 7 * * *}")
    @Transactional
    public void processExpiredEnrollments() {
        LocalDate today = LocalDate.now(clock);

        List<Enrollment> expired = enrollmentRepository.findExpired(today);

        if (expired.isEmpty()) {
            log.info("Enrollment expiration: no expired enrollments found as of {}", today);
            return;
        }

        Set<Long> memberIds = expired.stream()
                .map(e -> e.getMember().getId())
                .collect(Collectors.toSet());

        enrollmentRepository.deleteAll(expired);
        log.info("Enrollment expiration: deleted {} expired enrollment(s) as of {}", expired.size(), today);

        for (Long memberId : memberIds) {
            boolean hasActive = enrollmentRepository.existsActiveByMemberId(memberId, today);
            if (!hasActive) {
                try {
                    memberService.markInactive(memberId);
                    log.info("Enrollment expiration: member {} set to INACTIVE", memberId);
                } catch (MemberNotFoundException e) {
                    // Member deleted concurrently between the expiry query and the status flip.
                    // Skip it instead of rolling back the whole batch.
                    log.warn("Enrollment expiration: member {} no longer exists, skipping", memberId);
                }
            }
        }
    }
}
