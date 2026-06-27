package com.prahlad.aijobportal.common.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.prahlad.aijobportal.common.constant.CommonConstants.CORRELATION_ID_HEADER;

/**
 * Servlet filter (for non-reactive Spring MVC services) that:
 * 1. Reads the X-Correlation-Id header from the incoming request, if present
 *    (it will be present when the call originated from the Gateway or another
 *    internal service via Feign).
 * 2. Generates a new correlation ID if none was supplied (e.g. the very first
 *    entry point, or local direct testing via Swagger).
 * 3. Stores it in MDC so every log line for this request/thread includes it.
 * 4. Echoes it back on the response header for client-side traceability.
 *
 * This filter is registered automatically by each service via Spring's
 * component scanning (services include this common module on the classpath
 * and component-scan com.prahlad.aijobportal.common as part of their main application
 * package scan, OR explicitly register this bean in their security/web config).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = CorrelationIdUtil.generate();
            }
            CorrelationIdUtil.set(correlationId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            CorrelationIdUtil.clear();
        }
    }
}
