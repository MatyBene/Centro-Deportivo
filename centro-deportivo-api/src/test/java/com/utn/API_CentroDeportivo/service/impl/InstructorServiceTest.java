package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.response.InstructorSummaryDTO;
import com.utn.API_CentroDeportivo.model.entity.Instructor;
import com.utn.API_CentroDeportivo.model.entity.Member;
import com.utn.API_CentroDeportivo.model.entity.SportActivity;
import com.utn.API_CentroDeportivo.model.exception.InstructorNotFoundException;
import com.utn.API_CentroDeportivo.model.repository.users.IUserRepository;
import com.utn.API_CentroDeportivo.service.ICredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICredentialService credentialService;

    @InjectMocks
    private InstructorService instructorService;

    private Instructor instructor;
    private final Long instructorId = 1L;
    private final String instructorUsername = "usernameTest";
    private final String name = "nameTest";
    private final String lastname = "lastnameTest";
    private final String specialty = "specialtyTest";

    @BeforeEach
    void setUp() {
        instructor = new Instructor();
        instructor.setId(instructorId);
        instructor.setName(name);
        instructor.setLastname(lastname);
        instructor.setSpecialty(specialty);
        instructor.setActivities(Collections.singletonList(new SportActivity()));
    }

    @Nested
    class GetInstructorSummaryByIdTests {

        @Test
        void whenInstructorExists_ShouldReturnSummaryDTO() {
            // Arrange
            when(userRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

            // Act
            Optional<InstructorSummaryDTO> result = instructorService.getInstructorSummaryById(instructorId);

            // Assert
            assertTrue(result.isPresent());
            InstructorSummaryDTO dto = result.get();
            assertEquals("nameTest", dto.getName());
            assertEquals("specialtyTest", dto.getSpecialty());
            assertEquals(1, dto.getActivityCount());
        }

        @Test
        void whenUserIsNotInstructor_ShouldReturnEmptyOptional() {
            // Arrange
            Member member = new Member();
            member.setId(instructorId);
            when(userRepository.findById(instructorId)).thenReturn(Optional.of(member));

            // Act
            Optional<InstructorSummaryDTO> result = instructorService.getInstructorSummaryById(instructorId);

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        void whenInstructorNotFound_ShouldReturnEmptyOptional() {
            // Arrange
            when(userRepository.findById(instructorId)).thenReturn(Optional.empty());

            // Act
            Optional<InstructorSummaryDTO> result = instructorService.getInstructorSummaryById(instructorId);

            // Assert
            assertFalse(result.isPresent());
        }
    }

    @Nested
    class FindByUsernameTests {
        @Test
        void WhenInstructorExists_ShouldReturnInstructorEntity() {
            // Arrange
            when(credentialService.getUserByUsername(instructorUsername)).thenReturn(instructor);
            when(userRepository.findById(instructorId)).thenReturn(Optional.of(instructor));

            // Act
            Optional<Instructor> result = instructorService.findByUsername(instructorUsername);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(instructor, result.get());
        }

        @Test
        void WhenUserNotFound_ShouldThrowInstructorNotFoundException() {
            // Arrange
            when(credentialService.getUserByUsername(instructorUsername)).thenReturn(instructor);
            when(userRepository.findById(instructorId)).thenReturn(Optional.empty());

            // Act & Assert
            InstructorNotFoundException exception = assertThrows(InstructorNotFoundException.class, () -> {
                instructorService.findByUsername(instructorUsername);
            });
            assertEquals("Instructor no encontrado", exception.getMessage());
        }
    }

}