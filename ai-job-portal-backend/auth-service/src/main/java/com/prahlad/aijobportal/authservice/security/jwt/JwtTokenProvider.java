package com.prahlad.aijobportal.authservice.security.jwt;

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
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Issues and validates the platform's stateless JWT access tokens.
 * Claim keys follow the shared naming contract declared in
 * {@link CommonConstants} so every downstream microservice that validates
 * this token (via a shared verification filter added in a later phase)
 * reads the same claim names.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey accessKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey refreshKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UUID userId, String email, Set<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.getAccessTokenExpirationMs());

        return Jwts.builder()
                .subject(userId.toString())
                .claim(CommonConstants.CLAIM_USER_ID, userId.toString())
                .claim(CommonConstants.CLAIM_EMAIL, email)
                .claim(CommonConstants.CLAIM_ROLES, roles)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(accessKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.getRefreshTokenExpirationMs());

        return Jwts.builder()
                .subject(userId.toString())
                .claim(CommonConstants.CLAIM_USER_ID, userId.toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(refreshKey(), Jwts.SIG.HS256)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpirationMs() / 1000;
    }

    public long getRefreshTokenExpirationMs() {
        return jwtProperties.getRefreshTokenExpirationMs();
    }

    public Claims parseAccessTokenClaims(String token) {
        return Jwts.parser()
                .verifyWith(accessKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseRefreshTokenClaims(String token) {
        return Jwts.parser()
                .verifyWith(refreshKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessTokenValid(String token) {
        try {
            parseAccessTokenClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid access token: {}", ex.getMessage());
            return false;
        }
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
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
