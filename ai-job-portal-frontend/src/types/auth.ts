/**
 * Mirrors com.prahlad.aijobportal.authservice.user.enums.RoleName and
 * AccountStatus exactly (auth-service). Do not rename — used verbatim
 * in JWT roles claim and UserResponse.status.
 */
export type RoleName = "CANDIDATE" | "RECRUITER" | "ADMIN";

export type AccountStatus = "PENDING_VERIFICATION" | "ACTIVE" | "DISABLED";

/** Mirrors AuthResponse#UserResponse (auth-service). */
export interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: RoleName[];
  status: AccountStatus;
  emailVerified: boolean;
  createdAt: string;
}

/** Mirrors AuthResponse (auth-service). */
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
  user: UserResponse;
}

export type Theme = "light" | "dark" | "system";
