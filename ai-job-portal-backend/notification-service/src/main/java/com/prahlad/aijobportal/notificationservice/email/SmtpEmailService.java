package com.prahlad.aijobportal.notificationservice.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * SMTP-backed EmailService implementation using JavaMailSender, mirroring
 * the Auth Service SmtpEmailService convention. Email delivery failure
 * must never block the underlying notification write (the in-app
 * Notification row is always persisted first); failures are logged and
 * reflected in the Notification.status field instead of being thrown.
 */
@Service
@Slf4j
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpEmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void send(String toEmail, EmailContent content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(content.subject());
            helper.setText(content.htmlBody(), true);
            mailSender.send(message);
            log.info("Sent e-mail [{}] to {}", content.subject(), toEmail);
        } catch (MessagingException ex) {
            log.error("Failed to send e-mail [{}] to {}", content.subject(), toEmail, ex);
            throw new EmailDeliveryException("Failed to send e-mail to " + toEmail, ex);
        }
    }
}
