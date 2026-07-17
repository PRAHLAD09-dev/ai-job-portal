package com.prahlad.aijobportal.authservice.user.enums;

/**
 * How a {@link com.prahlad.aijobportal.authservice.user.entity.User}
 * account authenticates. Added for DAY12 "Google OAuth" — every account
 * created before this feature is {@code LOCAL} (the Flyway migration
 * defaults the column), and stays that way unless it's later linked to
 * a Google account via a matching, Google-verified email.
 */
public enum AuthProvider {
    /** Authenticates with an email + password set by the user. */
    LOCAL,
    /** Authenticates via Google Sign-In; may have no local password. */
    GOOGLE
}
