package com.prahlad.aijobportal.authservice.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis-backed cache for active refresh token hashes, used as a fast path
 * to check/revoke sessions without hitting PostgreSQL on every refresh
 * call. PostgreSQL ({@code refresh_tokens} table) remains the system of
 * record; Redis is purely an accelerator and never the only place a token
 * is tracked, per the project's Redis rules (caching/temporary data only,
 * never permanent business data).
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenCacheService {

    private static final String KEY_PREFIX = "auth:refresh-token:";

    private final StringRedisTemplate redisTemplate;

    public void store(String tokenHash, UUID userId, Duration ttl) {
        redisTemplate.opsForValue().set(KEY_PREFIX + tokenHash, userId.toString(), ttl);
    }

    public boolean exists(String tokenHash) {
        Boolean hasKey = redisTemplate.hasKey(KEY_PREFIX + tokenHash);
        return Boolean.TRUE.equals(hasKey);
    }

    public void evict(String tokenHash) {
        redisTemplate.delete(KEY_PREFIX + tokenHash);
    }
}
