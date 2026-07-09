import type { RoleName, UserResponse, AuthResponse } from "@/types/auth";

// Mirrors auth-service RegisterRequest
export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: Exclude<RoleName, "ADMIN" | "SUPER_ADMIN">; // registration only for CANDIDATE / RECRUITER
}

// Mirrors auth-service LoginRequest
export interface LoginRequest {
  email: string;
  password: string;
}

// Mirrors auth-service RefreshTokenRequest
export interface RefreshTokenRequest {
  refreshToken: string;
}

// Mirrors auth-service VerifyEmailRequest
export interface VerifyEmailRequest {
  token: string;
}

// Mirrors auth-service ResendVerificationRequest
export interface ResendVerificationRequest {
  email: string;
}

// Mirrors auth-service ForgotPasswordRequest
export interface ForgotPasswordRequest {
  email: string;
}

// Mirrors auth-service ResetPasswordRequest
export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

// Mirrors auth-service ChangePasswordRequest
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export type { UserResponse, AuthResponse };
