package com.prahlad.aijobportal.aiservice.security.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record AuthenticatedUser(UUID userId, String email, Set<String> roles) {

    public Set<GrantedAuthority> authorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
