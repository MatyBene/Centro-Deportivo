package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.response.EnrollmentDTO;
import com.utn.API_CentroDeportivo.model.entity.Enrollment;
import com.utn.API_CentroDeportivo.model.entity.Instructor;
import com.utn.API_CentroDeportivo.model.entity.Member;
import com.utn.API_CentroDeportivo.model.entity.SportActivity;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.*;
import com.utn.API_CentroDeportivo.model.repository.users.IEnrollmentRepository;
import com.utn.API_CentroDeportivo.model.repository.users.IUserRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.IMemberService;
import com.utn.API_CentroDeportivo.service.ISportActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private IEnrollmentRepository enrollmentRepository;

    @Mock
    private IMemberService memberService;

    @Mock
    private ICredentialService credentialService;

    @Mock
    private ISportActivityService sportActivityService;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Member member;
    private Instructor instructor;
    private SportActivity sportActivity;
    private Enrollment enrollment;
    private final String memberUsername = "testMember";
    private final String instructorUsername = "testInstructor";
    private final Long memberId = 1L;
    private final Status status = Status.INACTIVE;
    private final Long activityId = 1L;
    private final Integer maxMembers = 10;
    private final String activityName = "Yoga";
    private final Long instructorId = 1L;

    @BeforeEach
    void setUp() {
        instructor = new Instructor();
        instructor.setId(instructorId);

        member = new Member();
        member.setId(memberId);
        member.setStatus(status);

        sportActivity = new SportActivity();
        sportActivity.setId(activityId);
        sportActivity.setInstructor(instructor);
        sportActivity.setMaxMembers(maxMembers);
        sportActivity.setEnrollments(Collections.emptyList());
        sportActivity.setName(activityName);

        enrollment = Enrollment.builder()
                .id(1L)
                .member(member)
                .activity(sportActivity)
                .startDate(LocalDate.now())
                .build();
    }

    @Nested
    class EnrollMemberToActivityTests {
        @Test
        void whenNotEnrolled_ShouldSucceed() {
            // Arrange
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(member);
            when(sportActivityService.getSportActivityEntityById(activityId)).thenReturn(Optional.of(sportActivity));
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.empty());

            // Act
            enrollmentService.enrollMemberToActivity(memberUsername, activityId);

            // Assert
            verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
            verify(memberService, times(1)).updateMemberStatus(memberId);
        }

        @Test
        void whenAlreadyEnrolled_ShouldThrowMemberAlreadyEnrolledException() {
            // Arrange
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.of(enrollment));
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(member);
            when(sportActivityService.getSportActivityEntityById(activityId)).thenReturn(Optional.of(sportActivity));

            // Act & Assert
            assertThrows(MemberAlreadyEnrolledException.class, () -> {
                enrollmentService.enrollMemberToActivity(memberUsername, activityId);
            });
            verify(enrollmentRepository, never()).save(any(Enrollment.class));
        }
    }

    @Nested
    class UnsubscribeMemberFromActivityTests {
        @Test
        void whenIsLastEnrollment_ShouldSetStatusToInactive() {
            // Arrange
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.of(enrollment));
            when(enrollmentRepository.existsByMemberId(memberId)).thenReturn(false);
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(member);
            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

            // Act
            enrollmentService.unsubscribeMemberFromActivity(memberUsername, activityId);

            // Assert
            verify(enrollmentRepository, times(1)).delete(enrollment);
            verify(userRepository, times(1)).save(memberCaptor.capture());
            assertEquals(Status.INACTIVE, memberCaptor.getValue().getStatus());
        }

        @Test
        void whenEnrollmentNotFound_ShouldThrowEnrollmentNotFoundException() {
            // Arrange
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(member);
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EnrollmentNotFoundException.class, () -> {
                enrollmentService.unsubscribeMemberFromActivity(memberUsername, activityId);
            });

            verify(enrollmentRepository, never()).delete(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class EnrollMemberToActivityByInstructorTests {
        @Test
        void whenCapacityIsFull_ShouldThrowMaxCapacityException() {
            // Arrange
            sportActivity.setMaxMembers(0);
            when(credentialService.getUserByUsername(instructorUsername)).thenReturn(instructor);
            when(sportActivityService.getSportActivityEntityById(activityId)).thenReturn(Optional.of(sportActivity));

            // Act & Assert
            assertThrows(MaxCapacityException.class, () -> {
                enrollmentService.enrollMemberToActivityByInstructor(instructorUsername, activityId, memberId);
            });
        }

        @Test
        void whenActivityNotFound_ShouldThrowSportActivityNotFoundException() {
            // Arrange
            when(credentialService.getUserByUsername(instructorUsername)).thenReturn(instructor);
            when(sportActivityService.getSportActivityEntityById(activityId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(SportActivityNotFoundException.class, () -> {
                enrollmentService.enrollMemberToActivityByInstructor(instructorUsername, activityId, memberId);
            });
        }

        @Test
        void whenMemberNotFound_ShouldThrowMemberNotFoundException() {
            // Arrange
            when(credentialService.getUserByUsername(instructorUsername)).thenReturn(instructor);
            when(sportActivityService.getSportActivityEntityById(activityId)).thenReturn(Optional.of(sportActivity));
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.empty());
            when(userRepository.findById(memberId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MemberNotFoundException.class, () -> {
                enrollmentService.enrollMemberToActivityByInstructor(instructorUsername, activityId, memberId);
            });
        }
    }

    @Nested
    class CancelEnrollmentTests {
        @Test
        void whenIsLastEnrollment_ShouldDeleteAndSetMemberInactive() {
            // Arrange
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.of(enrollment));
            when(enrollmentRepository.existsByMemberId(memberId)).thenReturn(false);
            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

            // Act
            enrollmentService.cancelEnrollment(instructorId, activityId, memberId);

            // Assert
            verify(enrollmentRepository, times(1)).delete(enrollment);
            verify(userRepository, times(1)).save(memberCaptor.capture());
            assertEquals(Status.INACTIVE, memberCaptor.getValue().getStatus());
        }

        @Test
        void whenHasOtherEnrollments_ShouldDeleteWithoutChangingStatus() {
            // Arrange
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.of(enrollment));
            when(enrollmentRepository.existsByMemberId(memberId)).thenReturn(true);

            // Act
            enrollmentService.cancelEnrollment(instructorId, activityId, memberId);

            // Assert
            verify(enrollmentRepository, times(1)).delete(enrollment);
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenInstructorIsNotAuthorized_ShouldThrowUnauthorizedException() {
            // Arrange
            Long unauthorizedInstructorId = 2L;
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.of(enrollment));

            // Act & Assert
            assertThrows(UnauthorizedException.class, () -> {
                enrollmentService.cancelEnrollment(unauthorizedInstructorId, activityId, memberId);
            });
        }

        @Test
        void whenEnrollmentNotFound_ShouldThrowEnrollmentNotFoundException() {
            // Arrange
            when(enrollmentRepository.findByMemberIdAndActivityId(memberId, activityId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EnrollmentNotFoundException.class, () -> {
                enrollmentService.cancelEnrollment(instructorId, activityId, memberId);
            });

            verify(enrollmentRepository, never()).delete(any());
        }
    }


    @Nested
    class GetEnrollmentsByUsernameTests {
        @Test
        void whenEnrollmentsExist_ShouldReturnDtoList() {
            // Arrange
            when(enrollmentRepository.findByMemberId(memberId)).thenReturn(Collections.singletonList(enrollment));
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(member);

            // Act
            List<EnrollmentDTO> result = enrollmentService.getEnrollmentsByUsername(memberUsername);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Yoga", result.get(0).getActivityName());
        }
    }
}