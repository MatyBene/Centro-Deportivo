package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.entity.users.Credential;
import com.utn.API_CentroDeportivo.model.entity.users.Member;
import com.utn.API_CentroDeportivo.model.entity.users.User;
import com.utn.API_CentroDeportivo.model.repository.users.ICredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    @Mock
    private ICredentialRepository credentialRepository;

    @InjectMocks
    private CredentialService credentialService;

    private Credential credential;
    private User user;
    private final String existingUsername = "existingUser";
    private final String nonExistentUsername = "nonExistentUser";
    private final Long userId = 1L;
    private final Long credentialId = 1L;
    private final String name = "nameTest";
    private final String password = "Password123!";

    @BeforeEach
    void setUp() {
        user = new Member();
        user.setId(userId);
        user.setName(name);

        credential = new Credential();
        credential.setId(credentialId);
        credential.setUsername(existingUsername);
        credential.setPassword(password);
        credential.setUser(user);
    }

    @Nested
    class LoadUserByUsernameTests {
        @Test
        void whenUsersExists_ShouldReturnUserDetails() {
            // Arrange
            when(credentialRepository.findByUsername(existingUsername)).thenReturn(credential);

            // Act
            UserDetails userDetails = credentialService.loadUserByUsername(existingUsername);

            // Assert
            assertNotNull(userDetails);
            assertEquals(existingUsername, userDetails.getUsername());
            verify(credentialRepository, times(1)).findByUsername(existingUsername);
        }

        @Test
        void whenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
            // Arrange
            when(credentialRepository.findByUsername(nonExistentUsername)).thenReturn(null);

            // Act & Assert
            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
                credentialService.loadUserByUsername(nonExistentUsername);
            });
            assertTrue(exception.getMessage().contains("El nombre de usuario no existe: " + nonExistentUsername));
        }
    }

    @Nested
    class ExistsByUsernameTests {
        @Test
        void whenUsernameExists_ShouldReturnTrue() {
            // Arrange
            when(credentialRepository.existsByUsername(existingUsername)).thenReturn(true);

            // Act
            boolean result = credentialService.existsByUsername(existingUsername);

            // Assert
            assertTrue(result);
            verify(credentialRepository, times(1)).existsByUsername(existingUsername);
        }

        @Test
        void whenUsernameDoesNotExist_ShouldReturnFalse() {
            // Arrange
            when(credentialRepository.existsByUsername(nonExistentUsername)).thenReturn(false);

            // Act
            boolean result = credentialService.existsByUsername(nonExistentUsername);

            // Assert
            assertFalse(result);
            verify(credentialRepository, times(1)).existsByUsername(nonExistentUsername);
        }
    }

    @Nested
    class GetUserByUsernameTests {
        @Test
        void whenUserExists_ShouldReturnUserEntity() {
            // Arrange
            when(credentialRepository.findByUsername(existingUsername)).thenReturn(credential);

            // Act
            User foundUser = credentialService.getUserByUsername(existingUsername);

            // Assert
            assertNotNull(foundUser);
            assertEquals(user.getId(), foundUser.getId());
            assertEquals("nameTest", foundUser.getName());
        }
    }
}