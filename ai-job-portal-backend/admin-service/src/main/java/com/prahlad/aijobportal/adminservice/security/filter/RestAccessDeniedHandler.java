package com.prahlad.aijobportal.adminservice.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prahlad.aijobportal.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a consistent {@link ApiResponse} JSON envelope whenever an
 * authenticated-but-insufficiently-privileged request hits a
 * role-restricted endpoint (e.g. a CANDIDATE or RECRUITER token calling
 * an admin-only route, or an ADMIN calling a SUPER_ADMIN-only route).
 * Unlike the other services, Admin Service has real role gating beyond
 * "authenticated vs public", so — unlike its
 * {@code RestAuthenticationEntryPoint} sibling, which every service
 * already has — this 403 handler is a genuinely new concern here.
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.failure("You do not have permission to perform this action");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
