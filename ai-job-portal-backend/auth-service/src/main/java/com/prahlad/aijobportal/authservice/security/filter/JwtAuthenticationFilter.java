package com.prahlad.aijobportal.authservice.security.filter;

import com.prahlad.aijobportal.authservice.security.jwt.JwtTokenProvider;
import com.prahlad.aijobportal.authservice.security.userdetails.CustomUserDetailsService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Validates the {@code Authorization: Bearer <token>} header on every
 * request and, if valid, populates the Spring Security context so that
 * {@code GET /auth/me} and other protected endpoints can resolve the
 * current principal.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(CommonConstants.BEARER_PREFIX)) {
            String token = authHeader.substring(CommonConstants.BEARER_PREFIX.length());

            if (jwtTokenProvider.isAccessTokenValid(token)) {
                try {
                    Claims claims = jwtTokenProvider.parseAccessTokenClaims(token);
                    String email = claims.get(CommonConstants.CLAIM_EMAIL, String.class);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                            // The token's signature/expiry are still valid, but the
                            // account has been disabled/locked since the token was
                            // issued. Do not populate the SecurityContext: with no
                            // Authentication set, anyRequest().authenticated() in
                            // SecurityConfig rejects the request via
                            // RestAuthenticationEntryPoint (401), making every
                            // outstanding access token for this account unusable
                            // immediately - not just after it naturally expires.
                            log.debug("Rejecting JWT for disabled/locked account: {}", email);
                            filterChain.doFilter(request, response);
                            return;
                        }

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
