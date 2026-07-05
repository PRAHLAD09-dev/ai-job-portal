import { STORAGE_KEYS } from "@/constants/storage-keys";

/**
 * Thin wrapper around localStorage for auth tokens.
 * Per 02_FRONTEND_ARCHITECTURE.md security rules: JWT is the only
 * sensitive data allowed in Local Storage.
 */
export const tokenStorage = {
  getAccessToken: () => localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN),
  getRefreshToken: () => localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN),
  setTokens: (accessToken: string, refreshToken: string) => {
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
  },
  clear: () => {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
  },
};
