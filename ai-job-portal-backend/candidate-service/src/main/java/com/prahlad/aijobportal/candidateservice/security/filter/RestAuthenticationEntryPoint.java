package com.prahlad.aijobportal.candidateservice.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prahlad.aijobportal.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a consistent {@link ApiResponse} JSON envelope (instead of
 * Spring Security's default plain-text 401 body) whenever an
 * unauthenticated request hits a protected endpoint.
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.failure("Authentication is required to access this resource");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
