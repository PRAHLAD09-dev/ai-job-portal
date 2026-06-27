package com.prahlad.aijobportal.authservice.auth.entity;

import com.prahlad.aijobportal.authservice.config.BaseEntity;
import com.prahlad.aijobportal.authservice.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * One-time token e-mailed to a {@link User} who requested a password
 * reset. Single use: {@code usedAt} is set once consumed, after which the
 * token can never be reused, even if the expiry window has not elapsed.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"tokenHash"})
public class PasswordResetToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return usedAt == null && !isExpired();
    }
}
