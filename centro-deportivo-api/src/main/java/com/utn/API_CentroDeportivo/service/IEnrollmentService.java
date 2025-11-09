package com.utn.API_CentroDeportivo.service;

import com.utn.API_CentroDeportivo.model.dto.response.EnrollmentDTO;

import java.util.List;

public interface IEnrollmentService {
    void enrollMemberToActivity(String username, Long activityId);
    void unsubscribeMemberFromActivity(String username, Long activityId);
    List<EnrollmentDTO> getEnrollmentsByUsername(String username);
    void cancelEnrollment(Long instructorId, Long activityId, Long memberId);
    void enrollMemberToActivityByInstructor(String username, Long activityId, Long memberId);
    void enrollMemberToActivityByUsername(String instructorUsername, Long activityId, String memberUsername);
    void unenrollMemberFromActivityByUsername(String instructorUsername, Long activityId, String memberUsername);
}
