package com.prahlad.aijobportal.authservice.auth.service.impl;

import com.prahlad.aijobportal.authservice.auth.dto.request.LoginRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.RegisterRequest;
import com.prahlad.aijobportal.authservice.auth.dto.response.AuthResponse;
import com.prahlad.aijobportal.authservice.auth.dto.response.UserResponse;
import com.prahlad.aijobportal.authservice.auth.exception.AccountLockedException;
import com.prahlad.aijobportal.authservice.auth.exception.AccountNotVerifiedException;
import com.prahlad.aijobportal.authservice.auth.exception.InvalidCredentialsException;
import com.prahlad.aijobportal.authservice.auth.mapper.UserMapper;
import com.prahlad.aijobportal.authservice.auth.repository.EmailVerificationTokenRepository;
import com.prahlad.aijobportal.authservice.auth.repository.PasswordResetTokenRepository;
import com.prahlad.aijobportal.authservice.auth.repository.RefreshTokenRepository;
import com.prahlad.aijobportal.authservice.auth.service.RefreshTokenCacheService;
import com.prahlad.aijobportal.authservice.email.EmailService;
import com.prahlad.aijobportal.authservice.event.AuthEventPublisher;
import com.prahlad.aijobportal.authservice.event.dto.UserRegisteredEvent;
import com.prahlad.aijobportal.authservice.security.config.AuthProperties;
import com.prahlad.aijobportal.authservice.security.jwt.JwtTokenProvider;
import com.prahlad.aijobportal.authservice.user.entity.Role;
import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import com.prahlad.aijobportal.authservice.user.repository.RoleRepository;
import com.prahlad.aijobportal.authservice.user.repository.UserRepository;
import com.prahlad.aijobportal.common.exception.ResourceConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenCacheService refreshTokenCacheService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthEventPublisher authEventPublisher;
    @Mock
    private AuthProperties authProperties;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role candidateRole;

    @BeforeEach
    void setUp() {
        candidateRole = Role.builder().name(RoleName.CANDIDATE).build();
    }

    @Test
    void register_withSelfRegisterableRole_createsUserAndSendsVerificationEmail() {
        RegisterRequest request = new RegisterRequest(
                "Jane", "Doe", "Jane.Doe@Example.com", "StrongP@ss1", RoleName.CANDIDATE);

        when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.CANDIDATE)).thenReturn(Optional.of(candidateRole));
        when(passwordEncoder.encode("StrongP@ss1")).thenReturn("hashed-password");
        when(authProperties.getEmailVerificationTokenExpirationMs()).thenReturn(86400000L);
        when(authProperties.getFrontendVerifyEmailUrl()).thenReturn("http://localhost:5173/verify-email");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .email("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .status(AccountStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .build();
        savedUser.addRole(candidateRole);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(
                new UserResponse(savedUser.getId(), savedUser.getEmail(), "Jane", "Doe",
                        Set.of("CANDIDATE"), AccountStatus.PENDING_VERIFICATION, false, Instant.now()));

        UserResponse response = authService.register(request);

        assertThat(response.email()).isEqualTo("jane.doe@example.com");
        verify(emailService).sendEmailVerificationEmail(eq("jane.doe@example.com"), eq("Jane"), anyString());
        verify(applicationEventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void register_withAdminRole_throwsInvalidCredentialsException() {
        RegisterRequest request = new RegisterRequest(
                "Jane", "Doe", "jane.doe@example.com", "StrongP@ss1", RoleName.ADMIN);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_withExistingEmail_throwsResourceConflictException() {
        RegisterRequest request = new RegisterRequest(
                "Jane", "Doe", "jane.doe@example.com", "StrongP@ss1", RoleName.CANDIDATE);

        when(userRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResourceConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withCorrectCredentialsAndVerifiedAccount_returnsAuthResponse() {
        LoginRequest request = new LoginRequest("jane.doe@example.com", "StrongP@ss1");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("jane.doe@example.com")
                .passwordHash("hashed-password")
                .firstName("Jane")
                .lastName("Doe")
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .build();
        user.addRole(candidateRole);

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("StrongP@ss1", "hashed-password")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(), any(), any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");
        when(jwtTokenProvider.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(900L);
        when(userMapper.toUserResponse(user)).thenReturn(
                new UserResponse(user.getId(), user.getEmail(), "Jane", "Doe",
                        Set.of("CANDIDATE"), AccountStatus.ACTIVE, true, Instant.now()));

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenRepository).save(any());
        verify(refreshTokenCacheService).store(anyString(), eq(user.getId()), any());
    }

    @Test
    void login_withWrongPassword_throwsInvalidCredentialsExceptionAndIncrementsFailedAttempts() {
        LoginRequest request = new LoginRequest("jane.doe@example.com", "WrongPassword1!");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("jane.doe@example.com")
                .passwordHash("hashed-password")
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .build();

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword1!", "hashed-password")).thenReturn(false);
        when(authProperties.getMaxFailedLoginAttempts()).thenReturn(5);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository).save(user);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    void login_withUnverifiedAccount_throwsAccountNotVerifiedException() {
        LoginRequest request = new LoginRequest("jane.doe@example.com", "StrongP@ss1");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("jane.doe@example.com")
                .passwordHash("hashed-password")
                .status(AccountStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .accountLocked(false)
                .build();

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("StrongP@ss1", "hashed-password")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AccountNotVerifiedException.class);
    }

    @Test
    void login_withLockedAccount_throwsAccountLockedException() {
        LoginRequest request = new LoginRequest("jane.doe@example.com", "StrongP@ss1");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("jane.doe@example.com")
                .passwordHash("hashed-password")
                .status(AccountStatus.ACTIVE)
                .emailVerified(true)
                .accountLocked(true)
                .build();

        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AccountLockedException.class);

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void login_withNonExistentEmail_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("ghost@example.com", "StrongP@ss1");

        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
