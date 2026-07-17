import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { AuthResponse, UserResponse } from "@/types/auth";
import type {
  ChangePasswordRequest,
  ForgotPasswordRequest,
  GoogleAuthRequest,
  LoginRequest,
  RefreshTokenRequest,
  RegisterRequest,
  ResendVerificationRequest,
  ResetPasswordRequest,
  VerifyEmailRequest,
} from "@/features/auth/types";

/**
 * Maps 1:1 to auth-service's AuthController endpoints
 * (CommonConstants.API_BASE_PATH + "/auth"). No endpoint, request, or
 * response shape may be altered from the frontend, per
 * 04_BACKEND_INTEGRATION.md.
 */
export const authService = {
  register: (payload: RegisterRequest) =>
    apiClient
      .post<ApiResponse<UserResponse>>("/auth/register", payload)
      .then((res) => res.data),

  login: (payload: LoginRequest) =>
    apiClient.post<ApiResponse<AuthResponse>>("/auth/login", payload).then((res) => res.data),

  loginWithGoogle: (payload: GoogleAuthRequest) =>
    apiClient.post<ApiResponse<AuthResponse>>("/auth/oauth/google", payload).then((res) => res.data),

  refreshToken: (payload: RefreshTokenRequest) =>
    apiClient
      .post<ApiResponse<AuthResponse>>("/auth/refresh-token", payload)
      .then((res) => res.data),

  logout: (payload: RefreshTokenRequest) =>
    apiClient.post<ApiResponse<null>>("/auth/logout", payload).then((res) => res.data),

  verifyEmail: (payload: VerifyEmailRequest) =>
    apiClient.post<ApiResponse<null>>("/auth/verify-email", payload).then((res) => res.data),

  resendVerification: (payload: ResendVerificationRequest) =>
    apiClient
      .post<ApiResponse<null>>("/auth/resend-verification", payload)
      .then((res) => res.data),

  forgotPassword: (payload: ForgotPasswordRequest) =>
    apiClient.post<ApiResponse<null>>("/auth/forgot-password", payload).then((res) => res.data),

  resetPassword: (payload: ResetPasswordRequest) =>
    apiClient.post<ApiResponse<null>>("/auth/reset-password", payload).then((res) => res.data),

  changePassword: (payload: ChangePasswordRequest) =>
    apiClient.post<ApiResponse<null>>("/auth/change-password", payload).then((res) => res.data),

  getCurrentUser: () =>
    apiClient.get<ApiResponse<UserResponse>>("/auth/me").then((res) => res.data),
};
