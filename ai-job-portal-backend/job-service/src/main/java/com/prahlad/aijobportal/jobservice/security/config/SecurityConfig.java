package com.prahlad.aijobportal.jobservice.security.config;

import com.prahlad.aijobportal.jobservice.security.filter.JwtAuthenticationFilter;
import com.prahlad.aijobportal.jobservice.security.filter.RestAuthenticationEntryPoint;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless JWT-based security configuration for the Job Service.
 * Job browsing/search (GET on {@code /jobs/**} and
 * {@code /job-categories/**}) is public per DAY05's "Public APIs"
 * section; mutating operations and candidate-scoped resources (saved
 * jobs, alerts) require authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] PUBLIC_GET_ENDPOINTS = {
            CommonConstants.API_BASE_PATH + "/jobs",
            CommonConstants.API_BASE_PATH + "/jobs/search",
            CommonConstants.API_BASE_PATH + "/jobs/latest",
            CommonConstants.API_BASE_PATH + "/jobs/featured",
            CommonConstants.API_BASE_PATH + "/jobs/trending",
            CommonConstants.API_BASE_PATH + "/jobs/slug/**",
            CommonConstants.API_BASE_PATH + "/jobs/{jobId:[0-9a-fA-F\\-]{36}}",
            CommonConstants.API_BASE_PATH + "/jobs/{jobId:[0-9a-fA-F\\-]{36}}/similar",
            CommonConstants.API_BASE_PATH + "/job-categories",
            CommonConstants.API_BASE_PATH + "/job-categories/**"
    };

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
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
