package com.prahlad.aijobportal.authservice.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * {@link EmailService} implementation backed by Brevo's transactional
 * e-mail HTTPS API (https://api.brevo.com/v3/smtp/email) rather than raw
 * SMTP. Most PaaS free/hobby tiers (Railway included) block outbound SMTP
 * ports (25/465/587) to prevent spam, which made the previous
 * JavaMailSender-based implementation fail every send in production even
 * though it worked fine on localhost. Brevo's API is a normal HTTPS POST,
 * so it isn't affected by that restriction.
 */
@Service
@Slf4j
public class BrevoEmailService implements EmailService {

    private static final String BREVO_SEND_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;
    private final String fromName;

    public BrevoEmailService(
            RestClient.Builder restClientBuilder,
            @Value("${app.brevo.api-key:}") String apiKey,
            @Value("${app.mail.from}") String fromEmail,
            @Value("${app.mail.from-name:AI Job Portal}") String fromName) {
        this.restClient = restClientBuilder.build();
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    @Override
    public void sendEmailVerificationEmail(String toEmail, String firstName, String verificationLink) {
        String subject = "Verify your AI Job Portal account";
        String body = """
                <p>Hi %s,</p>
                <p>Thanks for registering with AI Job Portal. Please verify your e-mail address by clicking the link below:</p>
                <p><a href="%s">Verify my e-mail</a></p>
                <p>This link will expire in 24 hours. If you did not create this account, you can safely ignore this e-mail.</p>
                """.formatted(firstName, verificationLink);
        send(toEmail, subject, body);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String firstName, String resetLink) {
        String subject = "Reset your AI Job Portal password";
        String body = """
                <p>Hi %s,</p>
                <p>We received a request to reset your password. Click the link below to choose a new password:</p>
                <p><a href="%s">Reset my password</a></p>
                <p>This link will expire in 1 hour. If you did not request a password reset, you can safely ignore this e-mail.</p>
                """.formatted(firstName, resetLink);
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String htmlBody) {
        if (!StringUtils.hasText(apiKey)) {
            // No key configured (e.g. local dev without one set up yet) —
            // never block registration/login over a missing optional
            // integration; just skip the send and say so.
            log.warn("BREVO_API_KEY not set; skipping e-mail [{}] to {}", subject, toEmail);
            return;
        }
        try {
            restClient.post()
                    .uri(BREVO_SEND_URL)
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "sender", Map.of("name", fromName, "email", fromEmail),
                            "to", List.of(Map.of("email", toEmail)),
                            "subject", subject,
                            "htmlContent", htmlBody))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Sent e-mail [{}] to {} via Brevo", subject, toEmail);
        } catch (Exception ex) {
            // Email delivery failure must never block the underlying business
            // operation (registration / password reset already succeeded);
            // it is logged for operational visibility instead of propagated.
            log.error("Failed to send e-mail [{}] to {} via Brevo", subject, toEmail, ex);
        }
    }
}
