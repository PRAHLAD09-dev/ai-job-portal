package com.prahlad.aijobportal.authservice.security.filter;

import com.prahlad.aijobportal.common.constant.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Authenticates trusted, server-to-server calls made directly to
 * {@code /api/v1/auth/internal/**} (never routed through the API
 * Gateway) using a shared secret header, per DECISIONS.md ("Internal
 * JWT for Service-to-Service Communication").
 *
 * This is additive only — it never affects any existing user-facing
 * endpoint or the normal {@link JwtAuthenticationFilter} JWT flow.
 * Added specifically so the Notification Service, whose Kafka listeners
 * run with no incoming HTTP request and therefore no bearer token to
 * forward, can still resolve a user's e-mail address by calling
 * {@code getCurrentUser(userId)} through a REST endpoint rather than
 * needing direct database access across a service boundary (which
 * DECISIONS.md forbids).
 */
@Component
@Slf4j
public class InternalServiceAuthFilter extends OncePerRequestFilter {

    public static final String ROLE_INTERNAL_SERVICE = "INTERNAL_SERVICE";

    private final String internalServiceToken;

    public InternalServiceAuthFilter(@Value("${app.internal.service-token}") String internalServiceToken) {
        this.internalServiceToken = internalServiceToken;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(CommonConstants.INTERNAL_SERVICE_TOKEN_HEADER);

        if (token != null && !token.isBlank() && token.equals(internalServiceToken)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            var authentication = new UsernamePasswordAuthenticationToken(
                    "internal-service", null, List.of(new SimpleGrantedAuthority("ROLE_" + ROLE_INTERNAL_SERVICE)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
