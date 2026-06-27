package com.prahlad.aijobportal.authservice.auth.entity;

import com.prahlad.aijobportal.authservice.config.BaseEntity;
import com.prahlad.aijobportal.authservice.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * A persisted, hashed refresh token issued to a {@link User} session.
 * The raw token value is never stored — only its SHA-256 hash — so that
 * leaking the database does not expose usable bearer tokens.
 *
 * Tokens are also mirrored into Redis (see RefreshTokenCacheService) for
 * fast revocation lookups; PostgreSQL remains the source of truth so that
 * a cache flush never silently invalidates every active session.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"tokenHash"})
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return !revoked && !isExpired();
    }
}
