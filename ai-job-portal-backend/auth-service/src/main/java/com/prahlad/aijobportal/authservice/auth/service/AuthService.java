package com.prahlad.aijobportal.authservice.auth.service;

import com.prahlad.aijobportal.authservice.auth.dto.request.ChangePasswordRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.ForgotPasswordRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.GoogleAuthRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.LoginRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.RefreshTokenRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.RegisterRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.ResendVerificationRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.ResetPasswordRequest;
import com.prahlad.aijobportal.authservice.auth.dto.request.VerifyEmailRequest;
import com.prahlad.aijobportal.authservice.auth.dto.response.AuthResponse;
import com.prahlad.aijobportal.authservice.auth.dto.response.UserResponse;

import java.util.UUID;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    /** DAY12 "Google OAuth": verifies the ID token, then logs in an existing account or auto-registers a new one. */
    AuthResponse loginWithGoogle(GoogleAuthRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);

    void verifyEmail(VerifyEmailRequest request);

    void resendVerificationEmail(ResendVerificationRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(UUID userId, ChangePasswordRequest request);

    UserResponse getCurrentUser(UUID userId);
}
