package com.prahlad.aijobportal.common.correlation;

import org.slf4j.MDC;

import java.util.UUID;

import static com.prahlad.aijobportal.common.constant.CommonConstants.CORRELATION_ID_MDC_KEY;

/**
 * Utility for working with the correlation ID stored in SLF4J's MDC.
 * The correlation ID is generated once per inbound request (by
 * {@link CorrelationIdFilter} in each service, and originally at the
 * API Gateway), then propagated downstream via the
 * X-Correlation-Id header on every outgoing Feign call.
 */
public final class CorrelationIdUtil {

    private CorrelationIdUtil() {
        // Prevent instantiation
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static void set(String correlationId) {
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
    }

    public static String get() {
        return MDC.get(CORRELATION_ID_MDC_KEY);
    }

    public static void clear() {
        MDC.remove(CORRELATION_ID_MDC_KEY);
    }
}
