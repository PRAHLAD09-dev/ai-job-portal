package com.prahlad.aijobportal.notificationservice.email;

/**
 * Provider-independent abstraction for sending transactional
 * notification e-mails. Kept as an interface, per DECISIONS.md
 * ("Provider-Independent Design"), so the underlying provider (SMTP
 * today) can change without touching call sites.
 */
public interface EmailService {

    void send(String toEmail, EmailContent content);
}
