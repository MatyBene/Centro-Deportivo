package com.utn.API_CentroDeportivo.service.impl;

import com.utn.API_CentroDeportivo.model.dto.request.MemberEditDTO;
import com.utn.API_CentroDeportivo.model.dto.response.MembersDetailsDTO;
import com.utn.API_CentroDeportivo.model.entity.users.Credential;
import com.utn.API_CentroDeportivo.model.entity.users.Instructor;
import com.utn.API_CentroDeportivo.model.entity.users.Member;
import com.utn.API_CentroDeportivo.model.entity.users.User;
import com.utn.API_CentroDeportivo.model.enums.Status;
import com.utn.API_CentroDeportivo.model.exception.MemberNotFoundException;
import com.utn.API_CentroDeportivo.model.repository.users.IMemberRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IMemberRepository memberRepository;

    @Mock
    private ICredentialService credentialService;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private User userForCredential;
    private MemberEditDTO memberEditDTO;
    private final Long memberId = 1L;
    private final String memberUsername = "usernameTest";
    private final String nameOriginal = "Original Name";
    private final String lastnameOriginal = "Original Lastname";
    private final Status status = Status.INACTIVE;

    @BeforeEach
    void setUp() {
        Credential credential = new Credential();
        credential.setUsername(memberUsername);

        member = new Member();
        member.setId(memberId);
        member.setName(nameOriginal);
        member.setLastname(lastnameOriginal);
        member.setStatus(status);
        member.setCredential(credential);
        member.setEnrollments(Collections.emptyList());

        memberEditDTO = new MemberEditDTO(
                "New Name",
                "New Lastname",
                "123456789",
                "new@email.com",
                "2000-01-01",
                "12345678",
                memberUsername
        );

        userForCredential = new Member();
        userForCredential.setId(memberId);
    }

    @Nested
    class UpdateMemberStatusTests {
        @Test
        void whenMemberExists_ShouldUpdateStatusToActive() {
            // Arrange
            when(userRepository.findById(memberId)).thenReturn(Optional.of(member));
            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

            // Act
            memberService.updateMemberStatus(memberId);

            // Assert
            verify(userRepository, times(1)).save(memberCaptor.capture());
            assertEquals(Status.ACTIVE, memberCaptor.getValue().getStatus());
        }

        @Test
        void whenMemberNotFound_ShouldThrowMemberNotFoundException() {
            // Arrange
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MemberNotFoundException.class, () -> memberService.updateMemberStatus(memberId));
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class GetMemberByIdTests {
        @Test
        void whenMemberExists_ShouldReturnMemberOptional() {
            // Arrange
            when(userRepository.findById(memberId)).thenReturn(Optional.of(member));

            // Act
            Optional<Member> result = memberService.getMemberById(memberId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(member, result.get());
        }

        @Test
        void whenMemberNotFound_ShouldThrowMemberNotFoundException() {
            // Arrange
            when(userRepository.findById(memberId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(memberId));
        }
    }

    @Nested
    class UpdateMemberProfileTests {
        @Test
        void whenDtoHasNewData_ShouldUpdateMemberFields() {
            // Arrange
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(userForCredential);
            when(userRepository.findById(memberId)).thenReturn(Optional.of(member));
            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

            // Act
            memberService.updateMemberProfile(memberUsername, memberEditDTO);

            // Assert
            verify(userRepository, times(1)).save(memberCaptor.capture());
            Member savedMember = memberCaptor.getValue();
            assertEquals("New Name", savedMember.getName());
            assertEquals("New Lastname", savedMember.getLastname());
        }
    }

    @Nested
    class DeleteMemberByUsernameTests {
        @Test
        void whenMemberExists_ShouldDeleteMember() {
            // Arrange
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(userForCredential);
            when(userRepository.findById(memberId)).thenReturn(Optional.of(member));

            // Act
            memberService.deleteMemberByUsername(memberUsername);

            // Assert
            verify(userRepository, times(1)).delete(member);
        }

        @Test
        void whenMemberNotFound_ShouldThrowMemberNotFoundException() {
            // Arrange
            when(credentialService.getUserByUsername(memberUsername)).thenReturn(userForCredential);
            when(userRepository.findById(memberId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MemberNotFoundException.class, () -> memberService.deleteMemberByUsername(memberUsername));
        }
    }

    @Nested
    class GetMembersDetailsTests {
        @Test
        void getAllMembers_WhenCalled_ShouldReturnPageOfMemberDetailsDTO() {
            // Arrange
            Page<Member> memberPage = new PageImpl<>(Collections.singletonList(member));
            when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);

            // Act
            Page<MembersDetailsDTO> result = memberService.getAllMembers(0, 5);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(memberUsername, result.getContent().get(0).getUsername());
        }
    }

    @Nested
    class GetMemberByUsernameTests {
        @Test
        void whenMemberExists_ShouldReturnMemberDetailsDTO() {
            // Arrange
            when(userRepository.findById(memberId)).thenReturn(Optional.of(member));

            // Act
            MembersDetailsDTO result = memberService.getMemberDetailsById(memberId);

            // Assert
            assertNotNull(result);
            assertEquals(memberUsername, result.getUsername());
            assertEquals(nameOriginal, result.getName());
            assertEquals(lastnameOriginal, result.getLastname());
        }

        @Test
        void whenUserIsNotMember_ShouldThrowMemberNotFoundException() {
            // Arrange
            when(userRepository.findById(memberId)).thenReturn(Optional.of(new Instructor()));

            // Act & Assert
            assertThrows(MemberNotFoundException.class, () -> memberService.getMemberDetailsById(memberId));
        }
    }

    @Nested
    class MarkInactiveTests {
        @Test
        void whenMemberExists_ShouldSetInactive() {
            // Arrange
            Member activeMember = new Member();
            activeMember.setId(2L);
            activeMember.setStatus(Status.ACTIVE);
            when(userRepository.findById(2L)).thenReturn(Optional.of(activeMember));
            ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

            // Act
            memberService.markInactive(2L);

            // Assert
            verify(userRepository, times(1)).save(memberCaptor.capture());
            assertEquals(Status.INACTIVE, memberCaptor.getValue().getStatus());
        }

        @Test
        void whenMemberNotFound_ShouldThrowMemberNotFoundException() {
            // Arrange
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MemberNotFoundException.class, () -> memberService.markInactive(memberId));
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class SaveMemberTests {
        @Test
        void whenCalled_ShouldCallRepositorySave() {
            // Arrange
            Member newMember = new Member();

            // Act
            memberService.saveMember(newMember);

            // Assert
            verify(userRepository, times(1)).save(newMember);
        }
    }

}