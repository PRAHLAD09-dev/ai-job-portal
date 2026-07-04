package com.prahlad.aijobportal.notificationservice.email;

import org.springframework.stereotype.Component;

/**
 * Builds subject/body pairs for every transactional notification e-mail
 * this service sends, per DAY08_NOTIFICATION_SERVICE.md Email Templates
 * section. Plain HTML text blocks are used (no template engine
 * dependency), mirroring the convention already established by
 * SmtpEmailService in the Auth Service. Kept in a dedicated builder
 * (rather than inline in EmailService) so every template lives in one
 * reviewable place.
 */
@Component
public class EmailTemplateBuilder {

    public EmailContent welcome(String firstName) {
        String subject = "Welcome to AI Job Portal";
        String body = """
                <p>Hi %s,</p>
                <p>Welcome to AI Job Portal! Your account has been created successfully.</p>
                <p>You can now complete your profile, search jobs, and start applying.</p>
                """.formatted(firstName);
        return new EmailContent(subject, body);
    }

    public EmailContent passwordResetRequested(String firstName) {
        String subject = "Password reset requested for your account";
        String body = """
                <p>Hi %s,</p>
                <p>We noticed a password reset was requested for your AI Job Portal account.</p>
                <p>If this was you, please check your inbox for a separate e-mail with a reset link.
                If you did not request this, you can safely ignore this notice — your password has not been changed.</p>
                """.formatted(firstName);
        return new EmailContent(subject, body);
    }

    public EmailContent applicationSubmitted(String candidateName, String jobTitle) {
        String subject = "Application submitted: " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Your application for <strong>%s</strong> has been submitted successfully.</p>
                <p>You can track its status anytime from your dashboard.</p>
                """.formatted(candidateName, jobTitle);
        return new EmailContent(subject, body);
    }

    public EmailContent applicationShortlisted(String candidateName, String jobTitle) {
        String subject = "You have been shortlisted for " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Great news! You have been shortlisted for the <strong>%s</strong> position.</p>
                <p>The recruiter will be in touch with next steps soon.</p>
                """.formatted(candidateName, jobTitle);
        return new EmailContent(subject, body);
    }

    public EmailContent interviewScheduled(String candidateName, String jobTitle) {
        String subject = "Interview scheduled for " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Your application for <strong>%s</strong> has moved to the interview stage.</p>
                <p>Please check your dashboard for interview details.</p>
                """.formatted(candidateName, jobTitle);
        return new EmailContent(subject, body);
    }

    public EmailContent offerReleased(String candidateName, String jobTitle) {
        String subject = "Offer extended for " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Congratulations! You have received an offer for the <strong>%s</strong> position.</p>
                <p>Please log in to your dashboard to review the offer details.</p>
                """.formatted(candidateName, jobTitle);
        return new EmailContent(subject, body);
    }

    public EmailContent applicationRejected(String candidateName, String jobTitle) {
        String subject = "Update on your application for " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Thank you for applying for the <strong>%s</strong> position.</p>
                <p>After careful review, the recruiter has decided not to move forward with your application at this time.
                We encourage you to keep exploring other opportunities on AI Job Portal.</p>
                """.formatted(candidateName, jobTitle);
        return new EmailContent(subject, body);
    }

    public EmailContent candidateHired(String candidateName, String jobTitle) {
        String subject = "Congratulations! You have been hired for " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Congratulations! You have been hired for the <strong>%s</strong> position.</p>
                <p>Welcome aboard — the recruiter will reach out with onboarding details.</p>
                """.formatted(candidateName, jobTitle);
        return new EmailContent(subject, body);
    }

    public EmailContent resumeAnalysisReady(String candidateName, int atsScore) {
        String subject = "Your AI resume analysis is ready";
        String body = """
                <p>Hi %s,</p>
                <p>Your resume has been analyzed by our AI engine. Your current ATS score is <strong>%d</strong>.</p>
                <p>Log in to your dashboard to view detailed suggestions for improving your resume.</p>
                """.formatted(candidateName, atsScore);
        return new EmailContent(subject, body);
    }

    public EmailContent recommendationsReady(String candidateName, int recommendationCount) {
        String subject = "New AI job recommendations for you";
        String body = """
                <p>Hi %s,</p>
                <p>Our AI engine has generated <strong>%d</strong> new job recommendations tailored to your profile.</p>
                <p>Log in to your dashboard to explore them.</p>
                """.formatted(candidateName, recommendationCount);
        return new EmailContent(subject, body);
    }

    public EmailContent newJobPosted(String recruiterName, String jobTitle) {
        String subject = "Your job posting is now live: " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Your job posting <strong>%s</strong> has been created successfully and is now visible to candidates.</p>
                """.formatted(recruiterName, jobTitle);
        return new EmailContent(subject, body);
    }
}
