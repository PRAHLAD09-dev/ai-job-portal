package com.prahlad.aijobportal.authservice.auth.controller;

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
import com.prahlad.aijobportal.authservice.auth.service.AuthService;
import com.prahlad.aijobportal.authservice.security.userdetails.CustomUserPrincipal;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication APIs: registration, login, token lifecycle, e-mail
 * verification, and password management. Controllers contain no business
 * logic; they only translate HTTP requests/responses to/from the service
 * layer, per PROJECT_RULES.md Section 6.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, token management, and password operations")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new candidate or recruiter account")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful. Please check your email to verify your account.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Exchange a valid refresh token for a new access/refresh token pair")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke the supplied refresh token, ending the session")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify an account's email address using the token sent by email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend the email verification link")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerificationEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Verification email sent", null));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset link by email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists with this email, a password reset link has been sent", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using the token sent by email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change the authenticated user's password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user's profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UserResponse response = authService.getCurrentUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
