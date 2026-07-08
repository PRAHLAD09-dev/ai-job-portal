package com.prahlad.aijobportal.aiservice.security.filter;

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
 * {@code /api/v1/ai/internal/admin/**} (never routed through the API
 * Gateway) using a shared secret header, per DECISIONS.md ("Internal JWT
 * for Service-to-Service Communication"). Mirrors the identical pattern
 * already established in Auth Service's {@code InternalServiceAuthFilter},
 * added here so Admin Service can read AI usage statistics without
 * duplicating AI Service's business logic.
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
