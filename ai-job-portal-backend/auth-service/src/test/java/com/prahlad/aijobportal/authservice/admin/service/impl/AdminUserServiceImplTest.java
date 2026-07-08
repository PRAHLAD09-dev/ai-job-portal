package com.prahlad.aijobportal.authservice.admin.service.impl;

import com.prahlad.aijobportal.authservice.admin.dto.response.AdminUserResponse;
import com.prahlad.aijobportal.authservice.admin.mapper.AdminUserMapper;
import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.repository.UserRepository;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AdminUserMapper adminUserMapper;

    private AdminUserServiceImpl adminUserService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        adminUserService = new AdminUserServiceImpl(userRepository, adminUserMapper);
        userId = UUID.randomUUID();
        user = User.builder()
                .email("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .build();
    }

    @Test
    void disableUser_setsStatusToDisabled() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(adminUserMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return new AdminUserResponse(userId, u.getEmail(), u.getFirstName(), u.getLastName(),
                    Set.of(), u.getStatus(), u.isEmailVerified(), u.isAccountLocked(),
                    u.getFailedLoginAttempts(), Instant.now(), Instant.now());
        });

        AdminUserResponse response = adminUserService.disableUser(userId);

        assertThat(response.status()).isEqualTo(AccountStatus.DISABLED);
        assertThat(user.getStatus()).isEqualTo(AccountStatus.DISABLED);
    }

    @Test
    void getUser_whenUserDoesNotExist_throwsResourceNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.getUser(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void enableUser_resetsLockAndFailedAttempts() {
        user.setAccountLocked(true);
        user.setFailedLoginAttempts(5);
        user.setStatus(AccountStatus.DISABLED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(adminUserMapper.toResponse(any(User.class))).thenReturn(
                new AdminUserResponse(userId, user.getEmail(), user.getFirstName(), user.getLastName(),
                        Set.of(), AccountStatus.ACTIVE, true, false, 0, Instant.now(), Instant.now()));

        adminUserService.enableUser(userId);

        assertThat(user.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(user.isAccountLocked()).isFalse();
        assertThat(user.getFailedLoginAttempts()).isZero();
    }
}
