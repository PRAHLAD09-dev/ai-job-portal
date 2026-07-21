import axios, {
  type AxiosError,
  type InternalAxiosRequestConfig,
} from "axios";

import { ENV } from "@/constants/env";
import { ROUTES } from "@/constants/routes";
import { tokenStorage } from "@/lib/storage";

import type { ApiResponse } from "@/types/api";
import type { AuthResponse } from "@/types/auth";

/**
 * ------------------------------------------------------------------------
 * Global Axios Client
 * ------------------------------------------------------------------------
 * All frontend API requests MUST go through the API Gateway.
 * Never call fetch() or create another Axios instance.
 * Feature services should always import this client.
 * ------------------------------------------------------------------------
 */
export const apiClient = axios.create({
  baseURL: ENV.API_BASE_URL,
  timeout: 30000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

/**
 * ------------------------------------------------------------------------
 * Request Interceptor
 * ------------------------------------------------------------------------
 * Automatically attaches JWT access token.
 * Authorization header is skipped if no token exists.
 * ------------------------------------------------------------------------
 */
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = tokenStorage.getAccessToken();

    if (accessToken) {
      config.headers.set(
        "Authorization",
        `Bearer ${accessToken}`,
      );
    }

    return config;
  },

  (error) => Promise.reject(error),
);

/**
 * ------------------------------------------------------------------------
 * Refresh Token Queue
 * ------------------------------------------------------------------------
 * Prevents multiple refresh requests when many APIs
 * return 401 simultaneously.
 * ------------------------------------------------------------------------
 */

let isRefreshing = false;

let pendingQueue: Array<(token: string | null) => void> = [];

/**
 * Resolve all waiting requests.
 */
function resolveQueue(token: string | null) {
  pendingQueue.forEach((callback) => callback(token));
  pendingQueue = [];
}

/**
 * Clear authentication and redirect user.
 */
function forceLogout() {
  tokenStorage.clear();

  if (window.location.pathname !== ROUTES.LOGIN) {
    window.location.replace(ROUTES.LOGIN);
  }
}

/**
 * Returns true if request belongs to Auth Service.
 * These endpoints should never trigger refresh recursion.
 */
function isAuthenticationRequest(url?: string): boolean {
  if (!url) return false;

  return url.includes("/auth/");
}

/**
 * ------------------------------------------------------------------------
 * Response Interceptor
 * ------------------------------------------------------------------------
 * Handles:
 *
 * • Automatic Access Token Refresh
 * • Request Retry
 * • Refresh Queue
 * • Force Logout on Refresh Failure
 *
 * Matches Backend AuthController:
 *
 * POST /auth/refresh-token
 * Request  : RefreshTokenRequest
 * Response : ApiResponse<AuthResponse>
 * ------------------------------------------------------------------------
 */

apiClient.interceptors.response.use(
  (response) => response,

  async (error: AxiosError<ApiResponse<unknown>>) => {
    const originalRequest = error.config as
      | (InternalAxiosRequestConfig & { _retry?: boolean })
      | undefined;

    if (!originalRequest) {
      return Promise.reject(error);
    }

    const status = error.response?.status;

    /**
     * Ignore every Auth endpoint.
     * Login / Register / Forgot Password / Verify Email /
     * Refresh Token should never trigger refresh recursion.
     */
    if (isAuthenticationRequest(originalRequest.url)) {
      return Promise.reject(error);
    }

    /**
     * Only refresh once.
     */
    if (status !== 401 || originalRequest._retry) {
      return Promise.reject(error);
    }

    const refreshToken = tokenStorage.getRefreshToken();

    if (!refreshToken) {
      forceLogout();
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    /**
     * Another refresh request already running.
     * Wait until it finishes.
     */
    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        pendingQueue.push((newAccessToken) => {
          if (!newAccessToken) {
            reject(error);
            return;
          }

          originalRequest.headers.set(
            "Authorization",
            `Bearer ${newAccessToken}`,
          );

          resolve(apiClient(originalRequest));
        });
      });
    }

    isRefreshing = true;

    try {
      /**
       * Refresh Access Token
       */
      const response =
        await axios.post<ApiResponse<AuthResponse>>(
          `${ENV.API_BASE_URL}/auth/refresh-token`,
          {
            refreshToken,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          },
        );

      /**
       * Backend contract validation
       */
      if (!response.data.success || !response.data.data) {
        throw new Error(
          response.data.message ??
          "Unable to refresh access token.",
        );
      }

      const auth = response.data.data;

      tokenStorage.setTokens(
        auth.accessToken,
        auth.refreshToken,
      );

      /**
       * Wake every queued request.
       */
      resolveQueue(auth.accessToken);

      /**
       * Retry original request.
       */
      originalRequest.headers.set(
        "Authorization",
        `Bearer ${auth.accessToken}`,
      );

      return apiClient(originalRequest);
    } catch (refreshError) {
      /**
       * Refresh failed.
       * Reject every waiting request.
       */
      resolveQueue(null);

      forceLogout();

      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  },
);

/**
 * ------------------------------------------------------------------------
 * Extract a readable error message from backend ApiResponse.
 * ------------------------------------------------------------------------
 */
export function extractErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const response = error.response?.data as
      | ApiResponse<unknown>
      | undefined;

    if (response?.message?.trim()) {
      return response.message;
    }

    switch (error.response?.status) {
      case 400:
        return "Invalid request.";

      case 401:
        return "Your session has expired. Please login again.";

      case 403:
        return "You do not have permission to perform this action.";

      case 404:
        return "Requested resource was not found.";

      case 409:
        return "Resource already exists.";

      case 422:
        return "Validation failed.";

      case 429:
        return "Too many requests. Please try again later.";

      case 500:
        return "Internal server error.";

      case 502:
        return "Bad gateway.";

      case 503:
        return "Service temporarily unavailable.";

      case 504:
        return "Gateway timeout.";

      default:
        break;
    }

    /**
     * Network failure
     */
    if (!error.response) {
      return "Unable to connect to the server. Please check your internet connection.";
    }
  }

  if (error instanceof Error && error.message.trim()) {
    return error.message;
  }

  return "Something went wrong. Please try again.";
}

/**
 * ------------------------------------------------------------------------
 * Utility helpers
 * ------------------------------------------------------------------------
 */

export function isUnauthorizedError(error: unknown): boolean {
  return (
    axios.isAxiosError(error) &&
    error.response?.status === 401
  );
}

export function isForbiddenError(error: unknown): boolean {
  return (
    axios.isAxiosError(error) &&
    error.response?.status === 403
  );
}

export function isValidationError(error: unknown): boolean {
  return (
    axios.isAxiosError(error) &&
    error.response?.status === 422
  );
}

export function isConflictError(error: unknown): boolean {
  return (
    axios.isAxiosError(error) &&
    error.response?.status === 409
  );
}

export function isNotFoundError(error: unknown): boolean {
  return (
    axios.isAxiosError(error) &&
    error.response?.status === 404
  );
}

export function isServerError(error: unknown): boolean {
  return (
    axios.isAxiosError(error) &&
    (error.response?.status ?? 0) >= 500
  );
}
