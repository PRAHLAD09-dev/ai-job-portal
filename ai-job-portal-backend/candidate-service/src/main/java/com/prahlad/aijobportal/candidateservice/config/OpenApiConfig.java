package com.prahlad.aijobportal.candidateservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger metadata for the Candidate Service. UI is available at
 * {@code /swagger-ui.html}; raw spec at {@code /v3/api-docs}.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AI Job Portal - Candidate Service API",
                version = "v1",
                description = "Candidate profile, education, experience, skills, and resume management APIs.",
                contact = @Contact(name = "Prahlad")
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
