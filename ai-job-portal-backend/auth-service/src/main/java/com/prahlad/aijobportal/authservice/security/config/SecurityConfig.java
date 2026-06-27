package com.prahlad.aijobportal.authservice.security.config;

import com.prahlad.aijobportal.authservice.security.filter.JwtAuthenticationFilter;
import com.prahlad.aijobportal.authservice.security.filter.RestAuthenticationEntryPoint;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT-based security configuration for the Auth Service.
 * Session creation is disabled entirely; every request is authenticated
 * (or rejected) independently based on the bearer token.
 *
 * Note: login itself is performed directly in {@code AuthServiceImpl} via
 * {@link PasswordEncoder#matches}, not through Spring Security's
 * {@code AuthenticationManager}, since the service needs full control
 * over account-status checks (email verification, lockout) before and
 * after the credential check. No {@code AuthenticationManager} bean is
 * therefore declared here.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] PUBLIC_ENDPOINTS = {
            CommonConstants.API_BASE_PATH + "/auth/register",
            CommonConstants.API_BASE_PATH + "/auth/login",
            CommonConstants.API_BASE_PATH + "/auth/refresh-token",
            CommonConstants.API_BASE_PATH + "/auth/verify-email",
            CommonConstants.API_BASE_PATH + "/auth/resend-verification",
            CommonConstants.API_BASE_PATH + "/auth/forgot-password",
            CommonConstants.API_BASE_PATH + "/auth/reset-password",
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

