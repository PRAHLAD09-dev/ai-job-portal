package com.prahlad.aijobportal.adminservice.security.filter;

import com.prahlad.aijobportal.adminservice.security.jwt.JwtTokenValidator;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Validates the {@code Authorization: Bearer <token>} header issued by
 * the Auth Service and, if valid, populates the Spring Security context
 * with an {@link AuthenticatedUser} built from the token's claims.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(CommonConstants.BEARER_PREFIX)) {
            String token = authHeader.substring(CommonConstants.BEARER_PREFIX.length());

            if (jwtTokenValidator.isValid(token)) {
                try {
                    Claims claims = jwtTokenValidator.parseClaims(token);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        AuthenticatedUser principal = new AuthenticatedUser(
                                jwtTokenValidator.extractUserId(claims),
                                jwtTokenValidator.extractEmail(claims),
                                jwtTokenValidator.extractRoles(claims)
                        );

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(principal, null, principal.authorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception ex) {
                    log.debug("Could not set authentication from JWT: {}", ex.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
