/**
 * LocalStorage keys. Per 02_FRONTEND_ARCHITECTURE.md security rules,
 * only auth tokens and theme preference are persisted client-side.
 */
export const STORAGE_KEYS = {
  ACCESS_TOKEN: "ajp_access_token",
  REFRESH_TOKEN: "ajp_refresh_token",
  THEME: "ajp_theme",
} as const;
