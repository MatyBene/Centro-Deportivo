package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.entity.users.Enrollment;
import com.utn.API_CentroDeportivo.model.entity.users.Member;
import com.utn.API_CentroDeportivo.model.entity.users.SportActivity;
import com.utn.API_CentroDeportivo.service.IEmailService;
import com.utn.API_CentroDeportivo.model.repository.users.IEnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentExpirationNotificationServiceTest {

    @Mock
    private IEnrollmentRepository enrollmentRepository;

    @Mock
    private IEmailService emailService;

    @Mock
    private Clock clock;

    private EnrollmentExpirationNotificationService notificationService;

    private static final LocalDate FIXED_TODAY =
            LocalDate.of(2026, 8, 15);

    private static final LocalDate EXPIRATION_DATE =
            LocalDate.of(2026, 8, 20);

    private static final ZoneId ZONE_ID =
            ZoneId.of("America/Argentina/Buenos_Aires");

    @BeforeEach
    void setUp() {

        when(clock.instant())
                .thenReturn(
                        FIXED_TODAY
                                .atStartOfDay(ZONE_ID)
                                .toInstant()
                );

        when(clock.getZone())
                .thenReturn(ZONE_ID);

        notificationService =
                new EnrollmentExpirationNotificationService(
                        enrollmentRepository,
                        emailService,
                        clock,
                        5
                );
    }

    @Test
    void whenEnrollmentsExpireInFiveDays_shouldSendOneEmailPerMember() {

        // Arrange

        Member member1 = createMember(
                1L,
                "Tomas",
                "tomas@gmail.com"
        );

        Member member2 = createMember(
                2L,
                "Juan",
                "juan@gmail.com"
        );

        SportActivity swimming =
                createActivity(1L, "Natación");

        SportActivity gym =
                createActivity(2L, "Musculación");

        SportActivity football =
                createActivity(3L, "Fútbol");

        Enrollment enrollment1 =
                createEnrollment(
                        1L,
                        member1,
                        swimming
                );

        Enrollment enrollment2 =
                createEnrollment(
                        2L,
                        member1,
                        gym
                );

        Enrollment enrollment3 =
                createEnrollment(
                        3L,
                        member2,
                        football
                );

        List<Enrollment> enrollments =
                Arrays.asList(
                        enrollment1,
                        enrollment2,
                        enrollment3
                );

        when(enrollmentRepository.findEnrollmentsExpiringOn(EXPIRATION_DATE))
                .thenReturn(enrollments);

        // Act

        notificationService.notifyUpcomingExpirations();

        // Assert

        verify(emailService, times(2))
                .sendEmail(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void whenMemberHasMultipleExpiringEnrollments_shouldSendOnlyOneEmailContainingAllActivities() {

        // Arrange

        Member member =
                createMember(
                        1L,
                        "Tomas",
                        "tomas@gmail.com"
                );

        SportActivity swimming =
                createActivity(1L, "Natación");

        SportActivity gym =
                createActivity(2L, "Musculación");

        Enrollment enrollment1 =
                createEnrollment(
                        1L,
                        member,
                        swimming
                );

        Enrollment enrollment2 =
                createEnrollment(
                        2L,
                        member,
                        gym
                );

        when(enrollmentRepository.findEnrollmentsExpiringOn(EXPIRATION_DATE))
                .thenReturn(
                        Arrays.asList(
                                enrollment1,
                                enrollment2
                        )
                );

        // Act

        notificationService.notifyUpcomingExpirations();

        // Assert

        ArgumentCaptor<String> emailCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1))
                .sendEmail(
                        eq("tomas@gmail.com"),
                        anyString(),
                        emailCaptor.capture()
                );

        String emailContent =
                emailCaptor.getValue();

        assertTrue(emailContent.contains("Natación"));
        assertTrue(emailContent.contains("Musculación"));
    }

    @Test
    void whenEmailFailsForOneMember_shouldContinueSendingToOthers() {

        // Arrange

        Member member1 =
                createMember(
                        1L,
                        "Tomas",
                        "tomas@gmail.com"
                );

        Member member2 =
                createMember(
                        2L,
                        "Juan",
                        "juan@gmail.com"
                );

        SportActivity swimming =
                createActivity(1L, "Natación");

        SportActivity football =
                createActivity(2L, "Fútbol");

        Enrollment enrollment1 =
                createEnrollment(
                        1L,
                        member1,
                        swimming
                );

        Enrollment enrollment2 =
                createEnrollment(
                        2L,
                        member2,
                        football
                );

        when(enrollmentRepository.findEnrollmentsExpiringOn(EXPIRATION_DATE))
                .thenReturn(
                        Arrays.asList(
                                enrollment1,
                                enrollment2
                        )
                );

        doThrow(new RuntimeException("Error SMTP"))
                .when(emailService)
                .sendEmail(
                        eq("tomas@gmail.com"),
                        anyString(),
                        anyString()
                );

        // Act

        notificationService.notifyUpcomingExpirations();

        // Assert

        verify(emailService, times(1))
                .sendEmail(
                        eq("tomas@gmail.com"),
                        anyString(),
                        anyString()
                );

        verify(emailService, times(1))
                .sendEmail(
                        eq("juan@gmail.com"),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void whenThereAreNoUpcomingExpirations_shouldNotSendEmails() {

        // Arrange

        when(enrollmentRepository.findEnrollmentsExpiringOn(EXPIRATION_DATE))
                .thenReturn(Collections.emptyList());

        // Act

        notificationService.notifyUpcomingExpirations();

        // Assert

        verify(emailService, never())
                .sendEmail(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    private Member createMember(
            Long id,
            String name,
            String email) {

        Member member = new Member();

        member.setId(id);
        member.setName(name);
        member.setEmail(email);

        return member;
    }

    private SportActivity createActivity(
            Long id,
            String name) {

        SportActivity activity =
                new SportActivity();

        activity.setId(id);
        activity.setName(name);

        return activity;
    }

    private Enrollment createEnrollment(
            Long id,
            Member member,
            SportActivity activity) {

        Enrollment enrollment =
                new Enrollment();

        enrollment.setId(id);
        enrollment.setMember(member);
        enrollment.setActivity(activity);
        enrollment.setStartDate(
                LocalDate.of(2026, 8, 1)
        );
        enrollment.setEndDate(EXPIRATION_DATE);

        return enrollment;
    }
}