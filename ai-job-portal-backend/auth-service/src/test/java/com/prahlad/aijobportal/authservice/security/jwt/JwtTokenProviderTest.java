package com.prahlad.aijobportal.authservice.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-access-secret-key-must-be-at-least-32-bytes-long");
        properties.setRefreshSecret("test-refresh-secret-key-must-be-at-least-32-bytes-long");
        properties.setAccessTokenExpirationMs(900000L);
        properties.setRefreshTokenExpirationMs(604800000L);
        properties.setIssuer("ai-job-portal-auth-service-test");

        jwtTokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    void generateAccessToken_producesValidParsableToken() {
        UUID userId = UUID.randomUUID();
        String email = "candidate@example.com";
        Set<String> roles = Set.of("CANDIDATE");

        String token = jwtTokenProvider.generateAccessToken(userId, email, roles);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.isAccessTokenValid(token)).isTrue();
    }

    @Test
    void parseAccessTokenClaims_returnsExpectedClaims() {
        UUID userId = UUID.randomUUID();
        String email = "recruiter@example.com";
        Set<String> roles = Set.of("RECRUITER");

        String token = jwtTokenProvider.generateAccessToken(userId, email, roles);
        Claims claims = jwtTokenProvider.parseAccessTokenClaims(token);

        assertThat(jwtTokenProvider.extractUserId(claims)).isEqualTo(userId);
        assertThat(claims.get("email", String.class)).isEqualTo(email);
        assertThat(jwtTokenProvider.extractRoles(claims)).containsExactlyInAnyOrder("RECRUITER");
    }

    @Test
    void isAccessTokenValid_returnsFalseForGarbageToken() {
        assertThat(jwtTokenProvider.isAccessTokenValid("not-a-real-token")).isFalse();
    }

    @Test
    void isAccessTokenValid_returnsFalseWhenSignedWithDifferentSecret() {
        JwtProperties otherProperties = new JwtProperties();
        otherProperties.setSecret("a-completely-different-access-secret-key-of-sufficient-length");
        otherProperties.setRefreshSecret("a-completely-different-refresh-secret-key-of-sufficient-length");
        otherProperties.setAccessTokenExpirationMs(900000L);
        otherProperties.setRefreshTokenExpirationMs(604800000L);
        otherProperties.setIssuer("other-issuer");
        JwtTokenProvider otherProvider = new JwtTokenProvider(otherProperties);

        String token = otherProvider.generateAccessToken(UUID.randomUUID(), "x@example.com", Set.of("CANDIDATE"));

        assertThat(jwtTokenProvider.isAccessTokenValid(token)).isFalse();
    }

    @Test
    void generateRefreshToken_producesTokenParsableByRefreshKey() {
        UUID userId = UUID.randomUUID();

        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);
        Claims claims = jwtTokenProvider.parseRefreshTokenClaims(refreshToken);

        assertThat(jwtTokenProvider.extractUserId(claims)).isEqualTo(userId);
    }

    @Test
    void getAccessTokenExpirationSeconds_convertsMillisCorrectly() {
        assertThat(jwtTokenProvider.getAccessTokenExpirationSeconds()).isEqualTo(900L);
    }
}
