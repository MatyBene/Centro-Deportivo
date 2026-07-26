package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.request.UserRequestDTO;
import com.utn.API_CentroDeportivo.model.dto.response.AdminViewDTO;
import com.utn.API_CentroDeportivo.model.dto.response.UserDetailsDTO;
import com.utn.API_CentroDeportivo.model.entity.*;
import com.utn.API_CentroDeportivo.model.enums.PermissionLevel;
import com.utn.API_CentroDeportivo.model.enums.Role;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.InvalidFilterCombinationException;
import com.utn.API_CentroDeportivo.model.exception.UserNotFoundException;
import com.utn.API_CentroDeportivo.model.repository.users.IUserRepository;
import com.utn.API_CentroDeportivo.model.validation.AdminValidation;
import com.utn.API_CentroDeportivo.model.validation.InstructorValidation;
import com.utn.API_CentroDeportivo.service.IAuthService;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import com.utn.API_CentroDeportivo.service.IEnrollmentService;
import com.utn.API_CentroDeportivo.service.ISportActivityService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IAuthService authService;

    @Mock
    private ICredentialService credentialService;

    @Mock
    private ISportActivityService sportActivityService;

    @Mock
    private IEnrollmentService enrollmentService;

    @Mock
    private Validator validator;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminService adminService;

    private UserRequestDTO.UserRequestDTOBuilder userRequestBuilder;

    private Admin superAdmin, userManager;
    private Member member;
    private Instructor instructor;
    private final Long memberId = 1L;
    private final Long instructorId = 2L;
    private final Long superAdminId = 3L;
    private final Long userManagerId = 4L;
    private final String memberUsername = "testMember";

    @BeforeEach
    void setUp() {
        userRequestBuilder = UserRequestDTO.builder()
                .name("Test")
                .lastname("User")
                .dni("12345678")
                .birthdate("2000-01-01")
                .phone("123456")
                .email("test@mail.com")
                .username("testuser")
                .password("Password123!");

        member = new Member();
        member.setId(memberId);
        member.setStatus(Status.INACTIVE);
        Credential memberCredential = new Credential();
        memberCredential.setUsername(memberUsername);
        member.setCredential(memberCredential);

        instructor = new Instructor();
        instructor.setId(instructorId);

        superAdmin = new Admin();
        superAdmin.setId(superAdminId);
        superAdmin.setPermissionLevel(PermissionLevel.SUPER_ADMIN);

        userManager = new Admin();
        userManager.setId(userManagerId);
        userManager.setPermissionLevel(PermissionLevel.USER_MANAGER);
    }

    @Nested
    class CreateUserTests {
        @Test
        void whenRoleIsMember_ShouldSucceed() {
            // Arrange
            UserRequestDTO memberDto = userRequestBuilder.role(Role.MEMBER).build();
            when(validator.validate(any(UserRequestDTO.class))).thenReturn(Collections.emptySet());
            doNothing().when(authService).createAndSaveUser(any(User.class), any(Role.class));

            // Act
            adminService.createUser(memberDto);

            // Assert
            verify(authService, times(1)).createAndSaveUser(any(Member.class), eq(Role.MEMBER));
        }

        @Test
        void whenRoleIsInstructor_ShouldSucceed() {
            // Arrange
            UserRequestDTO instructorDto = userRequestBuilder
                    .role(Role.INSTRUCTOR)
                    .specialty("Yoga")
                    .build();
            when(validator.validate(any(UserRequestDTO.class))).thenReturn(Collections.emptySet());
            when(validator.validate(any(UserRequestDTO.class), eq(InstructorValidation.class))).thenReturn(Collections.emptySet());
            doNothing().when(authService).createAndSaveUser(any(User.class), any(Role.class));

            // Act
            adminService.createUser(instructorDto);

            // Assert
            verify(authService, times(1)).createAndSaveUser(any(Instructor.class), eq(Role.INSTRUCTOR));
        }

        @Test
        void whenRoleIsAdmin_ShouldSucceed() {
            // Arrange
            UserRequestDTO adminDto = userRequestBuilder
                    .role(Role.ADMIN)
                    .permissionLevel(PermissionLevel.USER_MANAGER)
                    .build();
            when(validator.validate(any(UserRequestDTO.class))).thenReturn(Collections.emptySet());
            when(validator.validate(any(UserRequestDTO.class), eq(AdminValidation.class))).thenReturn(Collections.emptySet());
            doNothing().when(authService).createAndSaveUser(any(User.class), any(Role.class));

            // Act
            adminService.createUser(adminDto);

            // Assert
            verify(authService, times(1)).createAndSaveUser(any(Admin.class), eq(Role.ADMIN));
        }

        @Test
        void whenDtoIsInvalid_ShouldThrowConstraintViolationException() {
            // Arrange
            Set<ConstraintViolation<UserRequestDTO>> violations = new HashSet<>();
            violations.add(mock(ConstraintViolation.class));
            when(validator.validate(any(UserRequestDTO.class))).thenReturn(violations);

            // Act & Assert
            assertThrows(ConstraintViolationException.class, () -> adminService.createUser(userRequestBuilder.build()));
            verify(authService, never()).createAndSaveUser(any(), any());
        }
    }

    @Nested
    class GetUsersTests {
        @Test
        void whenNoFiltersProvided_ShouldReturnAllUsers() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 5);
            Page<User> userPage = new PageImpl<>(Collections.singletonList(member));
            when(userRepository.findUsersByFilters(null, null, null, pageable)).thenReturn(userPage);

            // Act
            Page<AdminViewDTO> result = adminService.getUsers(null, null, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(userRepository, times(1)).findUsersByFilters(null, null, null, pageable);
        }

        @Test
        void whenFilterCombinationIsInvalid_ShouldThrowException() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            // Act & Assert
            assertThrows(InvalidFilterCombinationException.class, () -> {
                adminService.getUsers(Role.INSTRUCTOR, Status.ACTIVE, null, pageable);
            });
        }
    }

    @Nested
    class FindUserDetailsByUsernameTests {
        @Test
        void whenUserIsMember_ShouldReturnDetails() {
            // Arrange
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(member);
            when(userRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(enrollmentService.getEnrollmentsByUsername(memberUsername)).thenReturn(Collections.emptyList());

            // Act
            Optional<UserDetailsDTO> result = adminService.findUserDetailsByUsername(memberUsername);

            // Assert
            assertTrue(result.isPresent());
            verify(userRepository, times(1)).findById(memberId);
        }

        @Test
        void whenUserNotFound_ShouldThrowException() {
            // Arrange
            when(credentialService.getUserByUsername(anyString())).thenThrow(new UserNotFoundException("Usuario no encontrado."));

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> adminService.findUserDetailsByUsername("unknown"));
        }
    }

    @Nested
    class DeleteUserTests {
        @Nested
        class ByIdTests {
            @Test
            void whenDeletingAsSuperAdmin_ShouldSucceed() {
                // Arrange
                when(securityContext.getAuthentication()).thenReturn(authentication);
                SecurityContextHolder.setContext(securityContext);
                when(authentication.getName()).thenReturn("superadmin");

                when(credentialService.getUserByUsername("superadmin")).thenReturn(superAdmin);
                when(userRepository.findById(memberId)).thenReturn(Optional.of(member));

                // Act
                adminService.deleteUserById(memberId);

                // Assert
                verify(userRepository, times(1)).delete(member);
            }

            @Test
            void whenAdminDeletesSelf_ShouldThrowAccessDeniedException() {
                // Arrange
                when(securityContext.getAuthentication()).thenReturn(authentication);
                SecurityContextHolder.setContext(securityContext);
                when(authentication.getName()).thenReturn("superadmin");

                when(credentialService.getUserByUsername("superadmin")).thenReturn(superAdmin);
                when(userRepository.findById(superAdminId)).thenReturn(Optional.of(superAdmin));

                // Act & Assert
                assertThrows(AccessDeniedException.class, () -> adminService.deleteUserById(superAdminId));
            }
        }

        @Nested
        class ByUsernameTests {
            @Test
            void whenUserNotFound_ShouldThrowException() {
                // Arrange
                when(credentialService.getUserByUsername("unknown")).thenThrow(new UserNotFoundException("Usuario no encontrado."));

                // Act & Assert
                assertThrows(UserNotFoundException.class, () -> adminService.deleteUserByUsername("unknown"));
            }
        }
    }
}