import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { authService } from "@/features/auth/services/auth.service";
import { useAuth } from "@/hooks/useAuth";
import { extractErrorMessage } from "@/services/api-client";
import { ROUTES } from "@/constants/routes";
import type { RoleName } from "@/types/auth";
import type {
  ChangePasswordRequest,
  ForgotPasswordRequest,
  LoginRequest,
  RegisterRequest,
  ResendVerificationRequest,
  ResetPasswordRequest,
  VerifyEmailRequest,
} from "@/features/auth/types";

function dashboardPathForRole(roles: RoleName[]): string {
  if (roles.includes("ADMIN") || roles.includes("SUPER_ADMIN")) return ROUTES.ADMIN_DASHBOARD;
  if (roles.includes("RECRUITER")) return ROUTES.RECRUITER_DASHBOARD;
  return ROUTES.CANDIDATE_DASHBOARD;
}

export function useLogin() {
  const { loginSession } = useAuth();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (payload: LoginRequest) => authService.login(payload),
    onSuccess: (response) => {
      const { accessToken, refreshToken, user } = response.data;
      loginSession(accessToken, refreshToken, user);
      toast.success(response.message || "Login successful");
      navigate(dashboardPathForRole(user.roles), { replace: true });
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useRegister() {
  const navigate = useNavigate();
  return useMutation({
    mutationFn: (payload: RegisterRequest) => authService.register(payload),
    onSuccess: (response) => {
      toast.success(response.message || "Registration successful");
      navigate(ROUTES.LOGIN, { replace: true });
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useLogout() {
  const { logoutSession } = useAuth();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (refreshToken: string) => authService.logout({ refreshToken }),
    onSettled: () => {
      logoutSession();
      navigate(ROUTES.LOGIN, { replace: true });
    },
  });
}

export function useVerifyEmail() {
  return useMutation({
    mutationFn: (payload: VerifyEmailRequest) => authService.verifyEmail(payload),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useResendVerification() {
  return useMutation({
    mutationFn: (payload: ResendVerificationRequest) => authService.resendVerification(payload),
    onSuccess: (response) => toast.success(response.message || "Verification email sent"),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useForgotPassword() {
  return useMutation({
    mutationFn: (payload: ForgotPasswordRequest) => authService.forgotPassword(payload),
    onSuccess: (response) => toast.success(response.message),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useResetPassword() {
  const navigate = useNavigate();
  return useMutation({
    mutationFn: (payload: ResetPasswordRequest) => authService.resetPassword(payload),
    onSuccess: (response) => {
      toast.success(response.message || "Password reset successfully");
      navigate(ROUTES.LOGIN, { replace: true });
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useChangePassword() {
  return useMutation({
    mutationFn: (payload: ChangePasswordRequest) => authService.changePassword(payload),
    onSuccess: (response) => toast.success(response.message || "Password changed successfully"),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
