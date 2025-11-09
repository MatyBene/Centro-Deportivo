package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.response.EnrollmentDTO;
import com.utn.API_CentroDeportivo.model.entity.*;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.*;
import com.utn.API_CentroDeportivo.model.repository.IEnrollmentRepository;
import com.utn.API_CentroDeportivo.model.repository.IUserRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.IEnrollmentService;
import com.utn.API_CentroDeportivo.service.IMemberService;
import com.utn.API_CentroDeportivo.service.ISportActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService implements IEnrollmentService {

    @Autowired
    private IEnrollmentRepository enrollmentRepository;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private ICredentialService credentialService;

    @Autowired
    private ISportActivityService sportActivityService;

    @Autowired
    private IUserRepository userRepository;

    @Transactional
    public void enrollMemberToActivity(String username, Long activityId) {

        Member member = (Member) credentialService.getUserByUsername(username);
        SportActivity activity = sportActivityService.getSportActivityEntityById(activityId).get();

        if (enrollmentRepository.findByMemberIdAndActivityId(member.getId(), activityId).isPresent()) {
            throw new MemberAlreadyEnrolledException("El socio ya está inscripto en esta actividad");
        }

        Enrollment enrollment = Enrollment.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .member(member)
                .activity(activity)
                .build();

        enrollmentRepository.save(enrollment);
        memberService.updateMemberStatus(member.getId());
    }

    @Transactional
    @Override
    public void unsubscribeMemberFromActivity(String username, Long activityId) {
        Member member = (Member) credentialService.getUserByUsername(username);

        Enrollment enrollment = enrollmentRepository
                .findByMemberIdAndActivityId(member.getId(), activityId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Inscripción no encontrada"));

        enrollmentRepository.delete(enrollment);

        boolean hasOtherEnrollments = enrollmentRepository.existsByMemberId(member.getId());
        if (!hasOtherEnrollments) {
            member.setStatus(Status.INACTIVE);
            userRepository.save(member);
        }
    }

    public List<EnrollmentDTO> getEnrollmentsByUsername(String username) {
        Member member = (Member) credentialService.getUserByUsername(username);
        List<Enrollment> enrollments = enrollmentRepository.findByMemberId(member.getId());

        return enrollments.stream()
                .map(enrollment -> EnrollmentDTO.builder()
                        .activityName(enrollment.getActivity().getName())
                        .activityId(enrollment.getActivity().getId())
                        .startDate(enrollment.getStartDate())
                        .endDate(enrollment.getEndDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void cancelEnrollment(Long instructorId, Long activityId, Long memberId) {

        Enrollment enrollment = enrollmentRepository
                .findByMemberIdAndActivityId(memberId, activityId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Inscripción no encontrada"));

        SportActivity activity = enrollment.getActivity();
        if (!activity.getInstructor().getId().equals(instructorId)) {
            throw new UnauthorizedException("El instructor no tiene permiso para cancelar esta inscripción");
        }

        Member member = enrollment.getMember();

        enrollmentRepository.delete(enrollment);

        boolean hasOtherEnrollments = enrollmentRepository.existsByMemberId(member.getId());
        if (!hasOtherEnrollments) {
            member.setStatus(Status.INACTIVE);
            userRepository.save(member);
        }
    }
    @Transactional
    public void enrollMemberToActivityByInstructor(String username, Long activityId, Long memberId) {
        Instructor instructor = (Instructor) credentialService.getUserByUsername(username);

        SportActivity activity = sportActivityService.getSportActivityEntityById(activityId)
                .orElseThrow(() -> new SportActivityNotFoundException("Actividad no encontrada"));

        if (!activity.getInstructor().getId().equals(instructor.getId())) {
            throw new UnauthorizedException("No tienes permiso para inscribir en esta actividad");
        }

        if (activity.getEnrollments().size() >= activity.getMaxMembers()) {
            throw new MaxCapacityException("La actividad alcanzó el cupo máximo");
        }

        if (enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId).isPresent()) {
            throw new MemberAlreadyEnrolledException("El socio ya esta inscripto en esta actividad");
        }

        Member member = (Member) userRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Socio no encontrado"));

        Enrollment enrollment = Enrollment.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .member(member)
                .activity(activity)
                .build();

        enrollmentRepository.save(enrollment);
        memberService.updateMemberStatus(memberId);
    }

    @Transactional
    public void enrollMemberToActivityByUsername(String instructorUsername, Long activityId, String memberUsername) {
        Instructor instructor = (Instructor) credentialService.getUserByUsername(instructorUsername);
        SportActivity activity = sportActivityService.getSportActivityEntityById(activityId)
                .orElseThrow(() -> new SportActivityNotFoundException("Actividad no encontrada"));
        if (!activity.getInstructor().getId().equals(instructor.getId())) {
            throw new UnauthorizedException("No tienes permiso para inscribir en esta actividad");
        }
        if (activity.getEnrollments().size() >= activity.getMaxMembers()) {
            throw new MaxCapacityException("La actividad alcanzó el cupo máximo");
        }
        User memberUser = credentialService.getUserByUsername(memberUsername);
        if (!(memberUser instanceof Member)) {
            throw new MemberNotFoundException("El usuario proporcionado no es un socio (Member)");
        }
        Member member = (Member) memberUser;
        Long memberId = member.getId();
        if (enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId).isPresent()) {
            throw new MemberAlreadyEnrolledException("El socio ya está inscripto en esta actividad");
        }

        Enrollment enrollment = Enrollment.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .member(member)
                .activity(activity)
                .build();

        enrollmentRepository.save(enrollment);
        memberService.updateMemberStatus(memberId);
    }
}

