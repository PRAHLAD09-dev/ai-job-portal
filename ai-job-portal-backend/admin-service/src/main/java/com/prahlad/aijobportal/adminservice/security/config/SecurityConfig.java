package com.prahlad.aijobportal.adminservice.security.config;

import com.prahlad.aijobportal.adminservice.security.filter.JwtAuthenticationFilter;
import com.prahlad.aijobportal.adminservice.security.filter.RestAccessDeniedHandler;
import com.prahlad.aijobportal.adminservice.security.filter.RestAuthenticationEntryPoint;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT-based security configuration for the Admin Service.
 * Unlike every other service in the platform, this one has real
 * role-based authorization rather than a simple "authenticated vs
 * public" split, per DAY09_ADMIN_SERVICE.md's Roles section: every
 * {@code /api/v1/admin/**} endpoint requires the caller's JWT to carry
 * {@code ROLE_ADMIN} or {@code ROLE_SUPER_ADMIN} (candidates, recruiters,
 * and plain unauthenticated callers are rejected). Individual
 * super-privileged actions (e.g. permanently deleting a user) apply an
 * additional {@code @PreAuthorize("hasRole('SUPER_ADMIN')")} at the
 * controller/service method itself — enabled here via
 * {@link EnableMethodSecurity}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final RestAuthenticationEntryPoint authenticationEntryPoint;
        private final RestAccessDeniedHandler accessDeniedHandler;

        private static final String[] PUBLIC_ENDPOINTS = {
                        "/actuator/health",
                        "/actuator/info",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
        };

        private static final String ADMIN_ENDPOINTS = CommonConstants.API_BASE_PATH + "/admin/**";

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(handling -> handling
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                .requestMatchers(ADMIN_ENDPOINTS).hasAnyRole("ADMIN", "SUPER_ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
