package com.prahlad.aijobportal.authservice.security.userdetails;

import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Spring Security {@link UserDetails} adapter around the domain {@link User}
 * entity. Kept as a thin wrapper rather than implementing UserDetails
 * directly on the entity, to avoid leaking persistence concerns into the
 * security layer.
 */
public class CustomUserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final AccountStatus status;
    private final Set<GrantedAuthority> authorities;

    public CustomUserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.status = user.getStatus();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toSet());
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != AccountStatus.DISABLED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }
}
