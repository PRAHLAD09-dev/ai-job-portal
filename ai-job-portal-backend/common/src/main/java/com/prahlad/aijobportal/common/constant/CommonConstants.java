package com.prahlad.aijobportal.common.constant;

/**
 * Cross-cutting constants shared by all microservices.
 * Feature-specific constants (e.g. job status values, application status
 * values) belong inside their owning service, NOT here.
 */
public final class CommonConstants {

    private CommonConstants() {
        // Prevent instantiation
    }

    // ---- HTTP / Correlation ----
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    // ---- Auth headers propagated internally ----
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String INTERNAL_SERVICE_TOKEN_HEADER = "X-Internal-Service-Token";

    // ---- JWT claim keys (shared naming contract between Auth Service and all consumers) ----
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";

    // ---- Pagination defaults ----
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // ---- API base path ----
    public static final String API_BASE_PATH = "/api/v1";
}
