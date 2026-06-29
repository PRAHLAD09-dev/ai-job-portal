package com.prahlad.aijobportal.candidateservice.security.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The authenticated principal placed into the Spring Security context
 * once a request's JWT has been verified. Built purely from JWT claims
 * (userId, email, roles) — the Candidate Service has no local user table,
 * so there is nothing to look up; the Auth Service remains the sole
 * source of truth for identity.
 */
public record AuthenticatedUser(UUID userId, String email, Set<String> roles) {

    public Set<GrantedAuthority> authorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
