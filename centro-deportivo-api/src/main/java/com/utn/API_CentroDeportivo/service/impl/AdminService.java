package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.request.UserRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.response.AdminViewDTO;
import com.utn.API_CentroDeportivo.model.dto.response.EnrollmentDTO;
import com.utn.API_CentroDeportivo.model.dto.response.SportActivitySummaryDTO;
import com.utn.API_CentroDeportivo.model.dto.response.UserDetailsDTO;
import com.utn.API_CentroDeportivo.model.entity.*;
import com.utn.API_CentroDeportivo.model.enums.PermissionLevel;
import com.utn.API_CentroDeportivo.model.enums.Role;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.InvalidFilterCombinationException;
import com.utn.API_CentroDeportivo.model.exception.InvalidRoleException;
import com.utn.API_CentroDeportivo.model.exception.UserNotFoundException;
import com.utn.API_CentroDeportivo.model.mapper.AdminMapper;
import com.utn.API_CentroDeportivo.model.mapper.InstructorMapper;
import com.utn.API_CentroDeportivo.model.mapper.MemberMapper;
import com.utn.API_CentroDeportivo.model.repository.users.IUserRepository;
import com.utn.API_CentroDeportivo.model.validation.AdminValidation;
import com.utn.API_CentroDeportivo.model.validation.InstructorValidation;
import com.utn.API_CentroDeportivo.service.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminService implements IAdminService {

    @Autowired
    private Validator validator;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICredentialService credentialService;

    @Autowired
    private ISportActivityService sportActivityService;

    @Autowired
    private IEnrollmentService enrollmentService;

    @Override
    public void createUser(UserRequestDTO userDTO) {
        validate(userDTO);

        User user;
        switch (userDTO.getRole()) {
            case MEMBER:
                Member member = MemberMapper.mapToMember(userDTO);
                member.setStatus(Status.INACTIVE);
                user = member;
                break;
            case INSTRUCTOR:
                user = InstructorMapper.mapToInstructor(userDTO);
                break;
            case ADMIN:
                user = AdminMapper.mapToAdmin(userDTO);
                break;
            default:
                throw new InvalidRoleException("El rol especificado no es válido: " + userDTO.getRole());
        }

        Credential credential = Credential.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();
        user.setCredential(credential);

        authService.createAndSaveUser(user, userDTO.getRole());
    }

    @Override
    public Page<AdminViewDTO> getUsers(Role role, Status status, PermissionLevel permission, Pageable pageable) {
        Page<? extends User> userPage = userRepository.findUsersByFilters(role, status, permission, pageable);

        validateFilterCombination(role, status, permission);

        return userPage.map(user -> {
            if (user instanceof Admin admin) {
                return AdminMapper.toAdminViewDTO(admin);
            }
            if (user instanceof Instructor instructor) {
                return InstructorMapper.toAdminViewDTO(instructor);
            }
            if (user instanceof Member member) {
                return MemberMapper.toAdminViewDTO(member);
            }
            throw new IllegalArgumentException("Tipo de usuario desconocido: " + user.getClass());
        });
    }

    @Override
    public Optional<UserDetailsDTO> findUserDetailsByUsername(String username) {
        User user = userRepository.findById(credentialService.getUserByUsername(username).getId())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        if(user instanceof Member member){
            List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByUsername(username);
            return Optional.of(MemberMapper.mapToMembersDetailsDTO(member, enrollments));
        }

        if(user instanceof Instructor instructor){
            List<SportActivitySummaryDTO> activities = sportActivityService.getActivitiesByInstructor(instructor);
            return Optional.of(InstructorMapper.mapToInstructorDetailsDTO(instructor, activities));
        }

        if(user instanceof Admin admin){
            return Optional.of(AdminMapper.mapToAdminDetailsDTO(admin));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        checkDeletionPermission(userToDelete);
        userRepository.delete(userToDelete);
    }

    @Override
    @Transactional
    public void deleteUserByUsername(String username) {
        User userToDelete = credentialService.getUserByUsername(username);

        checkDeletionPermission(userToDelete);
        userRepository.delete(userToDelete);
    }

    private void checkDeletionPermission(User userToDelete) {
        String currentAdminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin currentAdmin = (Admin) credentialService.getUserByUsername(currentAdminUsername);
        PermissionLevel currentAdminPermission = currentAdmin.getPermissionLevel();

        if (currentAdmin.getId().equals(userToDelete.getId())) {
            throw new AccessDeniedException("Un administrador no puede eliminar su propia cuenta.");
        }

        if (currentAdminPermission == PermissionLevel.SUPER_ADMIN) {
            if (userToDelete instanceof Admin && ((Admin) userToDelete).getPermissionLevel() == PermissionLevel.SUPER_ADMIN) {
                throw new AccessDeniedException("Un SUPER_ADMIN no puede eliminar a otro SUPER_ADMIN.");
            }
            return;
        }

        if (currentAdminPermission == PermissionLevel.USER_MANAGER) {
            if (userToDelete instanceof Member || userToDelete instanceof Instructor) {
                return;
            } else {
                throw new AccessDeniedException("Un USER_MANAGER solo puede eliminar socios e instructores.");
            }
        }

        throw new AccessDeniedException("No tienes permisos para eliminar usuarios.");
    }

    private void validateFilterCombination(Role role, Status status, PermissionLevel permission) {
        if (permission != null && role != Role.ADMIN) {
            throw new InvalidFilterCombinationException("El filtro 'permission' solo es aplicable para el rol 'ADMIN'.");
        }
        if (status != null && role != Role.MEMBER) {
            throw new InvalidFilterCombinationException("El filtro 'status' solo es aplicable para el rol 'MEMBER'.");
        }
    }

    private void validate(UserRequestDTO userDTO) {
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (userDTO.getRole() == Role.INSTRUCTOR) {
            Set<ConstraintViolation<UserRequestDTO>> instructorViolations = validator.validate(userDTO, InstructorValidation.class);
            if (!instructorViolations.isEmpty()) {
                throw new ConstraintViolationException(instructorViolations);
            }
        }

        if (userDTO.getRole() == Role.ADMIN) {
            Set<ConstraintViolation<UserRequestDTO>> adminViolations = validator.validate(userDTO, AdminValidation.class);
            if (!adminViolations.isEmpty()) {
                throw new ConstraintViolationException(adminViolations);
            }
        }
    }
}
