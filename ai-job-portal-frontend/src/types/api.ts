/**
 * Mirrors com.prahlad.aijobportal.common.response.ApiResponse<T> and
 * ApiError exactly. Every backend endpoint returns this envelope shape,
 * per 04_BACKEND_INTEGRATION.md — do not alter these fields from the
 * frontend.
 */
export interface ApiError {
  field: string | null;
  code: string | null;
  message: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  errors: ApiError[] | null;
}

/** Mirrors com.prahlad.aijobportal.common.response.PageResponse<T> exactly. */
export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
