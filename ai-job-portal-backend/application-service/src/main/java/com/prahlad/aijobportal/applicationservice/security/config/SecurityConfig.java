package com.prahlad.aijobportal.applicationservice.security.config;

import com.prahlad.aijobportal.applicationservice.security.filter.InternalServiceAuthFilter;
import com.prahlad.aijobportal.applicationservice.security.filter.JwtAuthenticationFilter;
import com.prahlad.aijobportal.applicationservice.security.filter.RestAuthenticationEntryPoint;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT-based security configuration for the Application
 * Service. Every application endpoint requires authentication — unlike
 * Job Service, there is no public browsing surface here (applications
 * are always candidate- or recruiter-scoped), per
 * DAY06_APPLICATION_SERVICE.md's Security section.
 *
 * {@code /applications/internal/admin/**} is a separate, additive
 * concern (added for Admin Service, DAY09_ADMIN_SERVICE.md): it is
 * authenticated by {@link InternalServiceAuthFilter} via a shared
 * service-to-service secret (never a user bearer token) and restricted
 * to {@code ROLE_INTERNAL_SERVICE}, mirroring the identical pattern
 * already established in Auth Service. It must never be exposed through
 * the API Gateway's public routes.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InternalServiceAuthFilter internalServiceAuthFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private static final String INTERNAL_ENDPOINTS = CommonConstants.API_BASE_PATH + "/applications/internal/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(INTERNAL_ENDPOINTS).hasRole(InternalServiceAuthFilter.ROLE_INTERNAL_SERVICE)
                        .requestMatchers(CommonConstants.API_BASE_PATH + "/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(internalServiceAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
