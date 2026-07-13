package com.prahlad.aijobportal.notificationservice.email;

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
 * SMTP, mirroring the Auth Service BrevoEmailService convention. Most
 * PaaS free/hobby tiers (Railway included) block outbound SMTP ports
 * (25/465/587) to prevent spam, so a normal HTTPS POST is used instead.
 * Delivery failure is translated to {@link EmailDeliveryException} so the
 * caller (NotificationServiceImpl) can persist the Notification row with
 * status FAILED instead of losing the Kafka consumer offset commit.
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
    public void send(String toEmail, EmailContent content) {
        if (!StringUtils.hasText(apiKey)) {
            log.warn("BREVO_API_KEY not set; skipping e-mail [{}] to {}", content.subject(), toEmail);
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
                            "subject", content.subject(),
                            "htmlContent", content.htmlBody()))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Sent e-mail [{}] to {} via Brevo", content.subject(), toEmail);
        } catch (Exception ex) {
            log.error("Failed to send e-mail [{}] to {} via Brevo", content.subject(), toEmail, ex);
            throw new EmailDeliveryException("Failed to send e-mail to " + toEmail, ex);
        }
    }
}
