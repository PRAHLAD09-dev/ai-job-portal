package com.prahlad.aijobportal.candidateservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables {@code @CreatedDate} / {@code @LastModifiedDate} auditing support
 * for all entities extending {@link BaseEntity}.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
