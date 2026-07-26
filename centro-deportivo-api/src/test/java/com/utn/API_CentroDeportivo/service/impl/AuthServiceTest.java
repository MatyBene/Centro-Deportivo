package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.config.SecurityConfig;
import com.utn.API_CentroDeportivo.model.dto.request.MemberRequestDTO;
import com.utn.API_CentroDeportivo.model.entity.users.Credential;
import com.utn.API_CentroDeportivo.model.entity.users.Member;
import com.utn.API_CentroDeportivo.model.entity.users.User;
import com.utn.API_CentroDeportivo.model.enums.Role;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.FieldAlreadyExistsException;
import com.utn.API_CentroDeportivo.model.repository.users.IUserRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICredentialService credentialService;

    @Mock
    private SecurityConfig securityConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private MemberRequestDTO memberRequestDTO;
    private Member member;

    @BeforeEach
    void setUp() {
        memberRequestDTO = MemberRequestDTO.builder()
                .name("Max")
                .lastname("Verstappen")
                .dni("123456789")
                .birthdate("1800-09-06")
                .phone("0123456789")
                .email("mv@hotmail.com")
                .username("maxv1")
                .password("Password123!")
                .build();

        member = new Member();
        member.setName(memberRequestDTO.getName());
        member.setDni(memberRequestDTO.getDni());
        member.setEmail(memberRequestDTO.getEmail());
        Credential credential = new Credential();
        credential.setUsername(memberRequestDTO.getUsername());
        credential.setPassword(memberRequestDTO.getPassword());
        member.setCredential(credential);
    }

    @Nested
    class RegisterMemberTests {
        @Test
        void whenDataIsValid_ShouldSaveUserWithInactiveStatus(){
            // Arrange
            when(userRepository.existsByDni(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(credentialService.existsByUsername(anyString())).thenReturn(false);
            when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            // Act
            authService.registerMember(memberRequestDTO);

            // Assert
            verify(userRepository, times(1)).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertInstanceOf(Member.class, savedUser);
            assertEquals(Status.INACTIVE, ((Member) savedUser).getStatus());
            assertEquals("encodedPassword", savedUser.getCredential().getPassword());
        }
    }


    @Nested
    class CreateAndSaveUserTests {
        @Test
        void whenDniExists_ShouldThrowFieldAlreadyExistsException(){
            // Arrange
            when(userRepository.existsByDni(anyString())).thenReturn(true);

            // Act & Assert
            FieldAlreadyExistsException exception = assertThrows(FieldAlreadyExistsException.class, () -> {
                authService.createAndSaveUser(member, Role.MEMBER);
            });
            assertEquals("El campo ya está registrado", exception.getMessage());
            assertEquals("dni", exception.getField());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void whenEmailAlreadyExists_ShouldThrowFieldAlreadyExistsException() {
            // Arrange
            when(userRepository.existsByDni(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // Act & Assert
            FieldAlreadyExistsException exception = assertThrows(FieldAlreadyExistsException.class, () -> {
                authService.createAndSaveUser(member, Role.MEMBER);
            });
            assertEquals("El campo ya está registrado", exception.getMessage());
            assertEquals("email", exception.getField());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void whenUsernameAlreadyExists_ShouldThrowFieldAlreadyExistsException() {
            // Arrange
            when(userRepository.existsByDni(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(credentialService.existsByUsername(anyString())).thenReturn(true);

            // Act & Assert
            FieldAlreadyExistsException exception = assertThrows(FieldAlreadyExistsException.class, () -> {
                authService.createAndSaveUser(member, Role.MEMBER);
            });
            assertEquals("El campo ya está registrado", exception.getMessage());
            assertEquals("username", exception.getField());
            verify(userRepository, never()).save(any(User.class));
        }
    }



}