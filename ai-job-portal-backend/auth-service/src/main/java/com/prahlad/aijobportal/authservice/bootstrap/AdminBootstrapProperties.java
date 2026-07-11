package com.prahlad.aijobportal.authservice.bootstrap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@code app.admin-bootstrap.*} configuration properties, sourced from
 * the {@code ADMIN_BOOTSTRAP_*} environment variables. Used exclusively by
 * {@link AdminBootstrapRunner} to create the platform's first administrator
 * account on startup when no admin account exists yet.
 */
@Configuration
@ConfigurationProperties(prefix = "app.admin-bootstrap")
@Getter
@Setter
public class AdminBootstrapProperties {

    /**
     * Master switch. Defaults to {@code true}; the runner still no-ops
     * safely if email/password aren't supplied, but this allows explicitly
     * disabling the feature (e.g. in tests) without unsetting env vars.
     */
    private boolean enabled = true;

    private String email;

    private String password;

    private String firstName;

    private String lastName;
}
