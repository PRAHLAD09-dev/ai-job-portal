package com.prahlad.aijobportal.candidateservice.security.jwt;

import com.prahlad.aijobportal.common.constant.CommonConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Validates JWT access tokens issued by the Auth Service, using the same
 * shared signing secret and claim-key contract declared in
 * {@link CommonConstants}. This is read-only claims verification, not a
 * reimplementation of authentication business logic (login, password
 * checks, account lockout, etc. all remain exclusively in Auth Service);
 * every stateless microservice behind the Gateway necessarily performs
 * this same signature check to authenticate incoming requests.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenValidator {

    private final JwtProperties jwtProperties;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid access token: {}", ex.getMessage());
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String extractEmail(Claims claims) {
        return claims.get(CommonConstants.CLAIM_EMAIL, String.class);
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(Claims claims) {
        List<String> roles = claims.get(CommonConstants.CLAIM_ROLES, List.class);
        if (roles == null) {
            return Set.of();
        }
        return roles.stream().collect(Collectors.toSet());
    }
}
