package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.request.MemberEditDTO;
import com.utn.API_CentroDeportivo.model.dto.response.MembersDetailsDTO;
import com.utn.API_CentroDeportivo.model.entity.Member;
import com.utn.API_CentroDeportivo.model.entity.User;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.MemberNotFoundException;
import com.utn.API_CentroDeportivo.model.exception.FieldAlreadyExistsException;
import com.utn.API_CentroDeportivo.model.mapper.MemberMapper;
import com.utn.API_CentroDeportivo.model.repository.IMemberRepository;
import com.utn.API_CentroDeportivo.model.repository.IUserRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.IMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
public class MemberService implements IMemberService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IMemberRepository memberRepository;

    @Autowired
    private ICredentialService credentialService;

    @Transactional
    public void updateMemberStatus(Long memberId) {
        Member existingMember = (Member) userRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Socio no encontrado"));

        existingMember.setStatus(Status.ACTIVE);

        userRepository.save(existingMember);
    }

    @Override
    public Optional<Member> getMemberById(Long memberId) {
        return Optional.ofNullable((Member) userRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Socio no encontrado")));
    }

    @Transactional
    @Override
    public void updateMemberProfile(String username, MemberEditDTO dto) {
        Member member = (Member) userRepository.findById(credentialService.getUserByUsername(username).getId())
                .orElseThrow(( ) -> new MemberNotFoundException("Socio no encontrado"));

        if (dto.getName() != null) {
            member.setName(dto.getName());
        }
        if (dto.getLastname() != null) {
            member.setLastname(dto.getLastname());
        }
        if (dto.getPhone() != null) {
            member.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(member.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
                throw new FieldAlreadyExistsException("email", "El campo ya está registrado");
            }
            member.setEmail(dto.getEmail());
        }
        if (dto.getBirthdate() != null) {
            member.setBirthdate(dto.getBirthdate());
        }
        if (dto.getDni() != null) {
            if (!dto.getDni().equals(member.getDni()) && userRepository.existsByDni(dto.getDni())) {
                throw new FieldAlreadyExistsException("dni", "El campo ya está registrado");
            }
            member.setDni(dto.getDni());
        }

        userRepository.save(member);
    }
    @Transactional
    @Override
    public void deleteMemberByUsername(String username) {
        Member member = (Member) userRepository.findById(credentialService.getUserByUsername(username).getId())
                .orElseThrow(() -> new MemberNotFoundException("Socio no encontrado"));
        userRepository.delete(member);
    }
    @Override
    public Page<MembersDetailsDTO> getAllMembers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Member> members = memberRepository.findAll(pageable);
        return memberRepository.findAll(pageable)
                .map(MemberMapper::mapToMemberDetailsDTO);
    }
    @Override
    public MembersDetailsDTO getMemberDetailsById(Long memberId) {
        Member member = (Member) userRepository.findById(memberId)
                .filter(user -> user instanceof Member)
                .orElseThrow(() -> new MemberNotFoundException("Socio no encontrado"));

        return MemberMapper.mapToMemberDetailsDTO(member);
    }

    @Override
    public MembersDetailsDTO getMemberDetailsByUsername(String username) {
        Member member = (Member) userRepository.findById(credentialService.getUserByUsername(username).getId())
                .orElseThrow(() -> new MemberNotFoundException("Socio no encontrado"));

        return MemberMapper.mapToMemberDetailsDTO(member);
    }

    @Override
    public void saveMember(User member) {
        userRepository.save(member);
    }
}
