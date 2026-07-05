/**
 * Central place for reading Vite environment variables.
 * Never hardcode API URLs elsewhere — per 04_BACKEND_INTEGRATION.md
 * ("Never hardcode API URLs").
 */
export const ENV = {
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1",
  APP_NAME: import.meta.env.VITE_APP_NAME ?? "AI Job Portal",
  APP_SHORT_NAME: import.meta.env.VITE_APP_SHORT_NAME ?? "AJP",
} as const;
