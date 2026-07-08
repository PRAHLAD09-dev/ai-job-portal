package com.prahlad.aijobportal.adminservice.security.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The authenticated principal placed into the Spring Security context
 * once a request's JWT has been verified. Built purely from JWT claims
 * (userId, email, roles) — the Admin Service has no local user table, so
 * there is nothing to look up; the Auth Service remains the sole source
 * of truth for identity. Only principals carrying {@code ADMIN} or
 * {@code SUPER_ADMIN} in {@code roles} will ever pass this service's
 * {@code SecurityConfig} authorization rules.
 */
public record AuthenticatedUser(UUID userId, String email, Set<String> roles) {

    public Set<GrantedAuthority> authorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    public boolean isSuperAdmin() {
        return roles.contains("SUPER_ADMIN");
    }
}
