package com.prahlad.aijobportal.authservice.auth.service.impl;

import com.prahlad.aijobportal.authservice.auth.dto.request.ChangePasswordRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.ForgotPasswordRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.LoginRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.RefreshTokenRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.RegisterRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.ResendVerificationRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.ResetPasswordRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.VerifyEmailRequest;
import com.prahlad.aijobportal.authservice.auth.dto.response.AuthResponse;
import com.prahlad.aijobportal.authservice.auth.dto.response.UserResponse;
import com.prahlad.aijobportal.authservice.auth.entity.EmailVerificationToken;
import com.prahlad.aijobportal.authservice.auth.entity.PasswordResetToken;
import com.prahlad.aijobportal.authservice.auth.entity.RefreshToken;
import com.prahlad.aijobportal.authservice.auth.exception.AccountLockedException;
import com.prahlad.aijobportal.authservice.auth.exception.AccountNotVerifiedException;
import com.prahlad.aijobportal.authservice.auth.exception.InvalidCredentialsException;
import com.prahlad.aijobportal.authservice.auth.exception.InvalidTokenException;
import com.prahlad.aijobportal.authservice.auth.mapper.UserMapper;
import com.prahlad.aijobportal.authservice.auth.repository.EmailVerificationTokenRepository;
import com.prahlad.aijobportal.authservice.auth.repository.PasswordResetTokenRepository;
import com.prahlad.aijobportal.authservice.auth.repository.RefreshTokenRepository;
import com.prahlad.aijobportal.authservice.auth.service.AuthService;
import com.prahlad.aijobportal.authservice.auth.service.RefreshTokenCacheService;
import com.prahlad.aijobportal.authservice.auth.util.TokenHashUtil;
import com.prahlad.aijobportal.authservice.email.EmailService;
import com.prahlad.aijobportal.authservice.event.AuthEventPublisher;
import com.prahlad.aijobportal.authservice.event.dto.PasswordResetRequestedEvent;
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
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core Auth Service business logic: registration, login, token lifecycle,
 * e-mail verification, and password management. Business logic lives here
 * exclusively — controllers only translate HTTP <-> DTOs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenCacheService refreshTokenCacheService;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final AuthEventPublisher authEventPublisher;
    private final AuthProperties authProperties;

    private static final Set<RoleName> SELF_REGISTERABLE_ROLES = Set.of(RoleName.CANDIDATE, RoleName.RECRUITER);

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (!SELF_REGISTERABLE_ROLES.contains(request.role())) {
            throw new InvalidCredentialsException("Self-registration is only permitted for CANDIDATE or RECRUITER roles");
        }

        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResourceConflictException("An account with this email already exists");
        }

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", request.role()));

        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .status(AccountStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .build();
        user.addRole(role);

        User savedUser = userRepository.save(user);

        issueEmailVerificationToken(savedUser);

        authEventPublisher.publishUserRegistered(new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                role.getName().name(),
                Instant.now()
        ));

        log.info("Registered new user with id={}", savedUser.getId());

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.isAccountLocked() || user.getStatus() == AccountStatus.DISABLED) {
            throw new AccountLockedException("This account has been locked. Please contact support.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getStatus() == AccountStatus.PENDING_VERIFICATION || !user.isEmailVerified()) {
            throw new AccountNotVerifiedException("Please verify your email address before logging in");
        }

        resetFailedLoginAttempts(user);

        return issueAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String rawToken = request.refreshToken();

        Claims claims;
        try {
            claims = jwtTokenProvider.parseRefreshTokenClaims(rawToken);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        String tokenHash = TokenHashUtil.hash(rawToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid or has been revoked"));

        if (!storedToken.isUsable()) {
            throw new InvalidTokenException("Refresh token is invalid or has been revoked");
        }

        UUID userId = jwtTokenProvider.extractUserId(claims);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.isAccountLocked() || user.getStatus() == AccountStatus.DISABLED) {
            throw new AccountLockedException("This account has been locked. Please contact support.");
        }

        // Rotate: revoke the old refresh token and issue a brand-new pair,
        // preventing replay of a stolen-but-already-used refresh token.
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
        refreshTokenCacheService.evict(tokenHash);

        return issueAuthResponse(user);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        String tokenHash = TokenHashUtil.hash(request.refreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            refreshTokenCacheService.evict(tokenHash);
        });
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        String tokenHash = TokenHashUtil.hash(request.token());

        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Verification token is invalid"));

        if (!token.isUsable()) {
            throw new InvalidTokenException("Verification token is invalid or has expired");
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        token.setUsedAt(Instant.now());
        emailVerificationTokenRepository.save(token);

        log.info("Email verified for userId={}", user.getId());
    }

    @Override
    @Transactional
    public void resendVerificationEmail(ResendVerificationRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", normalizedEmail));

        if (user.isEmailVerified()) {
            throw new ResourceConflictException("This email address is already verified");
        }

        issueEmailVerificationToken(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        // Intentionally do not reveal whether the email exists: respond
        // the same way either way, only sending the email when it does.
        userRepository.findByEmail(normalizedEmail).ifPresent(user -> {
            String rawToken = TokenHashUtil.generateRawToken();
            String tokenHash = TokenHashUtil.hash(rawToken);

            PasswordResetToken token = PasswordResetToken.builder()
                    .user(user)
                    .tokenHash(tokenHash)
                    .expiresAt(Instant.now().plusMillis(authProperties.getPasswordResetTokenExpirationMs()))
                    .build();
            passwordResetTokenRepository.save(token);

            String resetLink = authProperties.getFrontendResetPasswordUrl() + "?token=" + rawToken;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetLink);

            authEventPublisher.publishPasswordResetRequested(new PasswordResetRequestedEvent(
                    user.getId(), user.getEmail(), user.getFirstName(), Instant.now()
            ));

            log.info("Password reset requested for userId={}", user.getId());
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = TokenHashUtil.hash(request.token());

        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Password reset token is invalid"));

        if (!token.isUsable()) {
            throw new InvalidTokenException("Password reset token is invalid or has expired");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        userRepository.save(user);

        token.setUsedAt(Instant.now());
        passwordResetTokenRepository.save(token);

        // Revoke every existing refresh token: a password reset must
        // invalidate all previously issued sessions.
        refreshTokenRepository.revokeAllByUserId(user.getId());

        log.info("Password reset completed for userId={}", user.getId());
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(user.getId());

        log.info("Password changed for userId={}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toUserResponse(user);
    }

    // ---- internal helpers ----

    private void issueEmailVerificationToken(User user) {
        String rawToken = TokenHashUtil.generateRawToken();
        String tokenHash = TokenHashUtil.hash(rawToken);

        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusMillis(authProperties.getEmailVerificationTokenExpirationMs()))
                .build();
        emailVerificationTokenRepository.save(token);

        String verificationLink = authProperties.getFrontendVerifyEmailUrl() + "?token=" + rawToken;
        emailService.sendEmailVerificationEmail(user.getEmail(), user.getFirstName(), verificationLink);
    }

    private AuthResponse issueAuthResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roleNames);
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        String refreshTokenHash = TokenHashUtil.hash(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(refreshTokenHash)
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMs()))
                .build();
        refreshTokenRepository.save(refreshToken);

        refreshTokenCacheService.store(
                refreshTokenHash,
                user.getId(),
                Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpirationMs())
        );

        return AuthResponse.of(
                accessToken,
                rawRefreshToken,
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                userMapper.toUserResponse(user)
        );
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= authProperties.getMaxFailedLoginAttempts()) {
            user.setAccountLocked(true);
            log.warn("Account locked due to repeated failed logins, userId={}", user.getId());
        }

        userRepository.save(user);
    }

    private void resetFailedLoginAttempts(User user) {
        if (user.getFailedLoginAttempts() != 0) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
    }
}
