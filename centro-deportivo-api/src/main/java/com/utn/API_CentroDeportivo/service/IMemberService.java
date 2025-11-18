package com.utn.API_CentroDeportivo.service;

import com.utn.API_CentroDeportivo.model.dto.request.MemberEditDTO;
import com.utn.API_CentroDeportivo.model.dto.response.MembersDetailsDTO;
import com.utn.API_CentroDeportivo.model.entity.Member;
import com.utn.API_CentroDeportivo.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IMemberService {
    void updateMemberStatus(Long memberId);
    Optional<Member> getMemberById(Long memberId);
    void saveMember(User member);
    void deleteMemberByUsername(String username);
    void updateMemberProfile(String username, MemberEditDTO dto);
    Page<MembersDetailsDTO> getAllMembers(int page, int size);
    MembersDetailsDTO getMemberDetailsById(Long memberId);
    MembersDetailsDTO getMemberDetailsByUsername(String username);
}
