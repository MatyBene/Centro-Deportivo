package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.entity.users.Enrollment;
import com.utn.API_CentroDeportivo.model.entity.users.Member;
import com.utn.API_CentroDeportivo.model.exception.MemberNotFoundException;
import com.utn.API_CentroDeportivo.model.repository.users.IEnrollmentRepository;
import com.utn.API_CentroDeportivo.service.IMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentExpirationServiceTest {

    @Mock
    private IEnrollmentRepository enrollmentRepository;

    @Mock
    private IMemberService memberService;

    @Mock
    private Clock clock;

    private EnrollmentExpirationService expirationService;

    private static final LocalDate FIXED_TODAY = LocalDate.of(2026, 8, 1);
    private static final ZoneId ZONE_ID = ZoneId.of("America/Argentina/Buenos_Aires");

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_TODAY.atStartOfDay(ZONE_ID).toInstant());
        when(clock.getZone()).thenReturn(ZONE_ID);

        expirationService = new EnrollmentExpirationService(enrollmentRepository, memberService, clock);
    }

    @Test
    void whenExpiredEnrollmentsExist_ShouldDeleteThem() {
        // Arrange
        Enrollment expired1 = createEnrollment(1L, 1L);
        Enrollment expired2 = createEnrollment(2L, 2L);
        List<Enrollment> expired = Arrays.asList(expired1, expired2);

        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(expired);
        when(enrollmentRepository.existsActiveByMemberId(1L, FIXED_TODAY)).thenReturn(false);
        when(enrollmentRepository.existsActiveByMemberId(2L, FIXED_TODAY)).thenReturn(false);

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, times(1)).deleteAll(expired);
        verify(memberService, times(1)).markInactive(1L);
        verify(memberService, times(1)).markInactive(2L);
    }

    @Test
    void whenEndDateIsToday_ShouldNotDelete() {
        // Arrange
        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(Collections.emptyList());

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, never()).deleteAll(any());
        verify(memberService, never()).markInactive(any());
    }

    @Test
    void whenNoExpiredEnrollments_ShouldNotCallDelete() {
        // Arrange
        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(Collections.emptyList());

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, never()).deleteAll(any());
        verify(memberService, never()).markInactive(any());
    }

    @Test
    void whenAllEnrollmentsExpired_ShouldSetMemberInactive() {
        // Arrange
        Enrollment expired = createEnrollment(1L, 1L);
        List<Enrollment> expiredList = Collections.singletonList(expired);

        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(expiredList);
        when(enrollmentRepository.existsActiveByMemberId(1L, FIXED_TODAY)).thenReturn(false);

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, times(1)).deleteAll(expiredList);
        verify(memberService, times(1)).markInactive(1L);
    }

    @Test
    void whenMemberHasRemainingActiveEnrollment_ShouldNotMarkInactive() {
        // Arrange
        Enrollment expired = createEnrollment(1L, 1L);
        List<Enrollment> expiredList = Collections.singletonList(expired);

        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(expiredList);
        when(enrollmentRepository.existsActiveByMemberId(1L, FIXED_TODAY)).thenReturn(true);

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, times(1)).deleteAll(expiredList);
        verify(memberService, never()).markInactive(any());
    }

    @Test
    void whenFixedClockUsed_ShouldUseInjectedClock() {
        // Arrange
        Enrollment expired = createEnrollment(1L, 1L);
        List<Enrollment> expiredList = Collections.singletonList(expired);

        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(expiredList);
        when(enrollmentRepository.existsActiveByMemberId(1L, FIXED_TODAY)).thenReturn(false);

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, times(1)).findExpired(FIXED_TODAY);
    }

    @Test
    void whenPreExistingExpiredData_ShouldSweepAll() {
        // Arrange
        Enrollment expired1 = createEnrollment(1L, 1L);
        Enrollment expired2 = createEnrollment(2L, 2L);
        Enrollment expired3 = createEnrollment(3L, 3L);
        List<Enrollment> expiredList = Arrays.asList(expired1, expired2, expired3);

        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(expiredList);
        when(enrollmentRepository.existsActiveByMemberId(1L, FIXED_TODAY)).thenReturn(false);
        when(enrollmentRepository.existsActiveByMemberId(2L, FIXED_TODAY)).thenReturn(false);
        when(enrollmentRepository.existsActiveByMemberId(3L, FIXED_TODAY)).thenReturn(false);

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, times(1)).deleteAll(expiredList);
        verify(memberService, times(1)).markInactive(1L);
        verify(memberService, times(1)).markInactive(2L);
        verify(memberService, times(1)).markInactive(3L);
    }

    @Test
    void whenMemberHasMixedExpiredAndActive_ShouldDeleteExpiredButNotMarkInactive() {
        // Arrange
        Enrollment expiredForMember1 = createEnrollment(1L, 1L);
        List<Enrollment> expiredList = Collections.singletonList(expiredForMember1);

        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(expiredList);
        when(enrollmentRepository.existsActiveByMemberId(1L, FIXED_TODAY)).thenReturn(true);

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, times(1)).deleteAll(expiredList);
        verify(memberService, never()).markInactive(1L);
    }

    @Test
    void whenMarkInactiveThrowsForOneMember_ShouldContinueWithOthers() {
        // Arrange
        Enrollment expired1 = createEnrollment(1L, 1L);
        Enrollment expired2 = createEnrollment(2L, 2L);
        List<Enrollment> expiredList = Arrays.asList(expired1, expired2);

        when(enrollmentRepository.findExpired(FIXED_TODAY)).thenReturn(expiredList);
        when(enrollmentRepository.existsActiveByMemberId(1L, FIXED_TODAY)).thenReturn(false);
        when(enrollmentRepository.existsActiveByMemberId(2L, FIXED_TODAY)).thenReturn(false);
        doThrow(new MemberNotFoundException("Socio no encontrado"))
                .when(memberService).markInactive(1L);

        // Act
        expirationService.processExpiredEnrollments();

        // Assert
        verify(enrollmentRepository, times(1)).deleteAll(expiredList);
        verify(memberService, times(1)).markInactive(1L);
        verify(memberService, times(1)).markInactive(2L);
    }

    private Enrollment createEnrollment(Long memberId, Long enrollmentId) {
        Member member = new Member();
        member.setId(memberId);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setMember(member);
        enrollment.setStartDate(LocalDate.of(2026, 6, 1));
        enrollment.setEndDate(LocalDate.of(2026, 7, 1));

        return enrollment;
    }
}
