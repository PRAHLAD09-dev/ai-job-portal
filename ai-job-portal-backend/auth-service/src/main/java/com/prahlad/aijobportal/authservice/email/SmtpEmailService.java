package com.prahlad.aijobportal.authservice.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * SMTP-backed {@link EmailService} implementation using
 * {@link JavaMailSender}. Sends simple HTML transactional e-mails directly
 * from the Auth Service for the two flows it owns (email verification,
 * password reset). A dedicated Notification Service will take over
 * broader notification concerns (job alerts, interview updates, etc.) in
 * a later development phase; this implementation deliberately stays
 * minimal and self-contained until then.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

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
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Sent e-mail [{}] to {}", subject, toEmail);
        } catch (MessagingException ex) {
            // Email delivery failure must never block the underlying business
            // operation (registration / password reset already succeeded);
            // it is logged for operational visibility instead of propagated.
            log.error("Failed to send e-mail [{}] to {}", subject, toEmail, ex);
        }
    }
}
