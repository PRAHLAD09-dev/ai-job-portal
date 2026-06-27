package com.prahlad.aijobportal.authservice.email;

/**
 * Provider-independent abstraction for sending transactional e-mails from
 * the Auth Service (email verification, password reset). Kept as an
 * interface so the underlying provider (SMTP today, a dedicated
 * Notification Service later) can change without touching call sites.
 */
public interface EmailService {

    void sendEmailVerificationEmail(String toEmail, String firstName, String verificationLink);

    void sendPasswordResetEmail(String toEmail, String firstName, String resetLink);
}
