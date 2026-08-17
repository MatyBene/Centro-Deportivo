package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.entity.users.Enrollment;
import com.utn.API_CentroDeportivo.model.repository.users.IEnrollmentRepository;
import com.utn.API_CentroDeportivo.service.IEmailService;
import com.utn.API_CentroDeportivo.service.IEnrollmentExpirationNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnrollmentExpirationNotificationService
        implements IEnrollmentExpirationNotificationService {

    private final IEnrollmentRepository enrollmentRepository;
    private final IEmailService emailService;
    private final Clock clock;
    private final int notificationDays;

    public EnrollmentExpirationNotificationService(
            IEnrollmentRepository enrollmentRepository,
            IEmailService emailService,
            Clock clock,
            @Value("${enrollment.expiration.notification.days:5}") int notificationDays) {

        this.enrollmentRepository = enrollmentRepository;
        this.emailService = emailService;
        this.clock = clock;
        this.notificationDays = notificationDays;
    }

    @Override
    @Scheduled(cron = "${enrollment.expiration.notification.cron:0 0 7 * * *}")
    public void notifyUpcomingExpirations() {

        LocalDate today = LocalDate.now(clock);
        LocalDate expirationDate = today.plusDays(notificationDays);

        log.info(
                "Enrollment expiration notification: searching enrollments expiring on {}",
                expirationDate
        );

        List<Enrollment> enrollments =
                enrollmentRepository.findEnrollmentsExpiringOn(expirationDate);

        if (enrollments.isEmpty()) {
            log.info(
                    "Enrollment expiration notification: no enrollments expiring in {} days",
                    notificationDays
            );
            return;
        }

        Map<Long, List<Enrollment>> enrollmentsByMember =
                enrollments.stream()
                        .collect(Collectors.groupingBy(
                                enrollment -> enrollment.getMember().getId()
                        ));

        log.info(
                "Enrollment expiration notification: {} member(s) to notify",
                enrollmentsByMember.size()
        );

        for (List<Enrollment> memberEnrollments : enrollmentsByMember.values()) {

            try {
                sendExpirationEmail(memberEnrollments);
            } catch (Exception e) {
                Long memberId = memberEnrollments.get(0).getMember().getId();

                log.error(
                        "Enrollment expiration notification: error sending email for member {}",
                        memberId,
                        e
                );
            }
        }
    }

    private void sendExpirationEmail(List<Enrollment> enrollments) {

        Enrollment firstEnrollment = enrollments.get(0);

        String email = firstEnrollment.getMember().getEmail();
        String memberName = firstEnrollment.getMember().getName();

        String subject = "Tus inscripciones están próximas a vencer";

        String htmlContent = buildExpirationEmail(
                memberName,
                enrollments
        );

        emailService.sendEmail(
                email,
                subject,
                htmlContent
        );

        log.info(
                "Enrollment expiration notification: email sent to {} for {} enrollment(s)",
                email,
                enrollments.size()
        );
    }

    private String buildExpirationEmail(
            String memberName,
            List<Enrollment> enrollments) {

        StringBuilder activitiesHtml = new StringBuilder();

        for (Enrollment enrollment : enrollments) {

            activitiesHtml.append("""
                    <li>
                        <strong>%s</strong><br>
                        Horario: %s - %s
                    </li>
                    """.formatted(
                    enrollment.getActivity().getName(),
                    enrollment.getActivity().getStartTime(),
                    enrollment.getActivity().getEndTime()
            ));
        }

        LocalDate expirationDate =
                enrollments.get(0).getEndDate();

        return """
                <html>
                    <body>
                        <h2>Hola %s</h2>

                        <p>
                            Te informamos que las siguientes inscripciones
                            están próximas a vencer:
                        </p>

                        <ul>
                            %s
                        </ul>

                        <p>
                            Fecha de vencimiento:
                            <strong>%s</strong>
                        </p>

                        <p>
                            Recordá renovar tus inscripciones para
                            continuar realizando las actividades.
                        </p>

                        <p>
                            Centro Deportivo
                        </p>
                    </body>
                </html>
                """.formatted(
                memberName,
                activitiesHtml,
                expirationDate
        );
    }
}