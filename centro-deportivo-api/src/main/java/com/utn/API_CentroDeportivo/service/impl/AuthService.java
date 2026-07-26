package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.config.SecurityConfig;
import com.utn.API_CentroDeportivo.model.dto.request.CredentialRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.request.MemberRequestDTO;
import com.utn.API_CentroDeportivo.model.entity.Credential;
import com.utn.API_CentroDeportivo.model.entity.Member;
import com.utn.API_CentroDeportivo.model.entity.User;
import com.utn.API_CentroDeportivo.model.enums.Role;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.FieldAlreadyExistsException;
import com.utn.API_CentroDeportivo.model.mapper.CredentialMapper;
import com.utn.API_CentroDeportivo.model.mapper.MemberMapper;
import com.utn.API_CentroDeportivo.model.repository.users.IUserRepository;
import com.utn.API_CentroDeportivo.service.IAuthService;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICredentialService credentialService;

    @Autowired
    private SecurityConfig securityConfig;

    @Transactional
    public void registerMember(MemberRequestDTO memberDTO) {
        Member member = MemberMapper.mapToMember(memberDTO);
        member.setStatus(Status.INACTIVE);
        Credential credential = Credential.builder().username(memberDTO.getUsername()).password(memberDTO.getPassword()).build();
        member.setCredential(credential);
        createAndSaveUser(member, Role.MEMBER);
    }

    @Transactional
    public void createAndSaveUser(User user, Role role) {
        validateUserFields(user);
        CredentialRequestDTO credentialDTO = CredentialRequestDTO.builder()
                .username(user.getCredential().getUsername())
                .password(securityConfig.passwordEncoder().encode(user.getCredential().getPassword()))
                .build();
        Credential credential = CredentialMapper.mapToCredential(credentialDTO, user);
        credential.setRole(role);
        user.setCredential(credential);
        userRepository.save(user);
    }

    private void validateUserFields(User user) {
        if (userRepository.existsByDni(user.getDni())) {
            throw new FieldAlreadyExistsException("dni", "El campo ya está registrado");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new FieldAlreadyExistsException("email", "El campo ya está registrado");
        }
        if (credentialService.existsByUsername(user.getCredential().getUsername())) {
            throw new FieldAlreadyExistsException("username", "El campo ya está registrado");
        }
    }

}
