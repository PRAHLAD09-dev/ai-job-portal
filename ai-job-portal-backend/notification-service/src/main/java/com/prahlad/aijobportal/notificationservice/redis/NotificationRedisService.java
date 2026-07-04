package com.prahlad.aijobportal.notificationservice.redis;

import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Redis-backed read models for the Notification Service, per
 * DAY08_NOTIFICATION_SERVICE.md Redis section ("Unread Count",
 * "Latest Notifications"). Both are maintained incrementally alongside
 * the PostgreSQL writes (never as the source of truth) so a cache miss
 * or Redis outage degrades gracefully rather than losing data — callers
 * always fall back to a database read when a key is absent.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRedisService {

    private static final String UNREAD_COUNT_KEY_PREFIX = "notification:unread-count:";
    private static final String LATEST_NOTIFICATIONS_KEY_PREFIX = "notification:latest:";
    private static final int LATEST_NOTIFICATIONS_MAX_SIZE = 10;
    private static final Duration TTL = Duration.ofHours(24);

    private final RedisTemplate<String, Object> redisTemplate;

    public void incrementUnreadCount(UUID userId) {
        try {
            String key = unreadCountKey(userId);
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, TTL);
        } catch (Exception ex) {
            log.warn("Failed to increment Redis unread count for userId={}", userId, ex);
        }
    }

    public void decrementUnreadCount(UUID userId) {
        try {
            String key = unreadCountKey(userId);
            Long value = redisTemplate.opsForValue().decrement(key);
            if (value != null && value < 0) {
                redisTemplate.opsForValue().set(key, 0L, TTL);
            }
        } catch (Exception ex) {
            log.warn("Failed to decrement Redis unread count for userId={}", userId, ex);
        }
    }

    public void resetUnreadCount(UUID userId, long actualCount) {
        try {
            redisTemplate.opsForValue().set(unreadCountKey(userId), actualCount, TTL);
        } catch (Exception ex) {
            log.warn("Failed to reset Redis unread count for userId={}", userId, ex);
        }
    }

    /**
     * Returns the cached unread count, or {@code null} on a cache miss —
     * callers must fall back to {@code NotificationRepository.countByUserIdAndReadFalse}
     * and repopulate via {@link #resetUnreadCount}.
     */
    public Long getUnreadCount(UUID userId) {
        try {
            Object value = redisTemplate.opsForValue().get(unreadCountKey(userId));
            if (value == null) {
                return null;
            }
            return Long.valueOf(value.toString());
        } catch (Exception ex) {
            log.warn("Failed to read Redis unread count for userId={}", userId, ex);
            return null;
        }
    }

    public void pushLatestNotification(UUID userId, NotificationResponse notification) {
        try {
            String key = latestNotificationsKey(userId);
            redisTemplate.opsForList().leftPush(key, notification);
            redisTemplate.opsForList().trim(key, 0, LATEST_NOTIFICATIONS_MAX_SIZE - 1);
            redisTemplate.expire(key, TTL);
        } catch (Exception ex) {
            log.warn("Failed to push latest notification to Redis for userId={}", userId, ex);
        }
    }

    public List<NotificationResponse> getLatestNotifications(UUID userId) {
        try {
            List<Object> raw = redisTemplate.opsForList().range(latestNotificationsKey(userId), 0, LATEST_NOTIFICATIONS_MAX_SIZE - 1);
            if (raw == null) {
                return List.of();
            }
            return raw.stream()
                    .filter(Objects::nonNull)
                    .map(NotificationResponse.class::cast)
                    .toList();
        } catch (Exception ex) {
            log.warn("Failed to read latest notifications from Redis for userId={}", userId, ex);
            return List.of();
        }
    }

    private String unreadCountKey(UUID userId) {
        return UNREAD_COUNT_KEY_PREFIX + userId;
    }

    private String latestNotificationsKey(UUID userId) {
        return LATEST_NOTIFICATIONS_KEY_PREFIX + userId;
    }
}
