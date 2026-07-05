import { createContext, useCallback, useEffect, useMemo, useState, type ReactNode } from "react";
import { authService } from "@/features/auth/services/auth.service";
import { tokenStorage } from "@/lib/storage";
import type { UserResponse } from "@/types/auth";

interface AuthContextValue {
  user: UserResponse | null;
  isAuthenticated: boolean;
  isInitializing: boolean;
  setUser: (user: UserResponse | null) => void;
  loginSession: (accessToken: string, refreshToken: string, user: UserResponse) => void;
  logoutSession: () => void;
  refetchCurrentUser: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isInitializing, setIsInitializing] = useState(true);

  const loginSession = useCallback(
    (accessToken: string, refreshToken: string, nextUser: UserResponse) => {
      tokenStorage.setTokens(accessToken, refreshToken);
      setUser(nextUser);
    },
    [],
  );

  const logoutSession = useCallback(() => {
    tokenStorage.clear();
    setUser(null);
  }, []);

  const refetchCurrentUser = useCallback(async () => {
    try {
      const response = await authService.getCurrentUser();
      setUser(response.data);
    } catch {
      tokenStorage.clear();
      setUser(null);
    }
  }, []);

  // On app boot: if a token exists, hydrate the current user (Day 01
  // "Current User" global-state requirement in 02_FRONTEND_ARCHITECTURE.md).
  useEffect(() => {
    const bootstrap = async () => {
      const token = tokenStorage.getAccessToken();
      if (token) {
        await refetchCurrentUser();
      }
      setIsInitializing(false);
    };
    void bootstrap();
  }, [refetchCurrentUser]);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: Boolean(user),
      isInitializing,
      setUser,
      loginSession,
      logoutSession,
      refetchCurrentUser,
    }),
    [user, isInitializing, loginSession, logoutSession, refetchCurrentUser],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
