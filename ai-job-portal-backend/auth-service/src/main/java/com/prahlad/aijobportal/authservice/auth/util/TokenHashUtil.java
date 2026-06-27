package com.prahlad.aijobportal.authservice.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Utility for hashing opaque tokens (refresh, e-mail verification,
 * password reset) before persisting them, and for generating
 * cryptographically secure random token values.
 *
 * Raw token values are only ever held in memory long enough to be
 * returned to the caller (response body or e-mail link) — the database
 * always stores the SHA-256 hash, never the raw value.
 */
public final class TokenHashUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private TokenHashUtil() {
    }

    public static String generateRawToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }
}
