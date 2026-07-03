package com.prahlad.aijobportal.aiservice.security.config;

import com.prahlad.aijobportal.aiservice.security.filter.JwtAuthenticationFilter;
import com.prahlad.aijobportal.aiservice.security.filter.RestAuthenticationEntryPoint;
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
 * Stateless JWT-based security configuration for the AI Service. Every
 * AI endpoint requires authentication — per DAY07_AI_SERVICE.md's
 * Security section, a candidate may only analyze their own resume and
 * a recruiter may only generate AI content for their own company; both
 * rules require a known authenticated principal.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(CommonConstants.API_BASE_PATH + "/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
