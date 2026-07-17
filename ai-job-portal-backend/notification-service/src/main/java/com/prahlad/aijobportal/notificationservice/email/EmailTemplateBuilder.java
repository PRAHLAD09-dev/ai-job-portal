package com.prahlad.aijobportal.notificationservice.email;

import com.prahlad.aijobportal.common.email.EmailLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Builds subject/body pairs for every transactional notification e-mail
 * this service sends, per DAY08_NOTIFICATION_SERVICE.md's Email
 * Templates section and upgraded to responsive branded HTML for
 * DAY12_..._Production_Polish.md's "Email Templates" feature. Every
 * template delegates its outer shell to {@link EmailLayout} (shared with
 * Auth Service) and only builds its own inner content fragment - so
 * every e-mail this platform sends looks like the same product. Kept in
 * a dedicated builder (rather than inline in EmailService) so every
 * template lives in one reviewable place.
 */
@Component
public class EmailTemplateBuilder {

    private final String dashboardUrl;

    public EmailTemplateBuilder(@Value("${app.frontend.dashboard-url}") String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public EmailContent welcome(String firstName) {
        String subject = "Welcome to AI Job Portal";
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Welcome to AI Job Portal! Your account has been created successfully.</p>
                <p style="margin:0 0 16px;">You can now complete your profile, search jobs, and start applying.</p>
                %s
                """.formatted(firstName, EmailLayout.button("Go to my dashboard", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("Your AI Job Portal account is ready", body));
    }

    public EmailContent passwordResetRequested(String firstName) {
        String subject = "Password reset requested for your account";
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">We noticed a password reset was requested for your AI Job Portal account.</p>
                %s
                """.formatted(
                firstName,
                EmailLayout.note("If this was you, check your inbox for a separate e-mail with a reset link. "
                        + "If you did not request this, you can safely ignore this notice — your password has not been changed.")
        );
        return new EmailContent(subject, EmailLayout.render("Password reset was requested for your account", body));
    }

    public EmailContent applicationSubmitted(String candidateName, String jobTitle) {
        String subject = "Application submitted: " + jobTitle;
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Your application for <strong>%s</strong> has been submitted successfully.</p>
                <p style="margin:0 0 16px;">You can track its status anytime from your dashboard.</p>
                %s
                """.formatted(candidateName, jobTitle, EmailLayout.button("Track my application", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("Your application for " + jobTitle + " was submitted", body));
    }

    public EmailContent applicationShortlisted(String candidateName, String jobTitle) {
        String subject = "You have been shortlisted for " + jobTitle;
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Great news! You have been shortlisted for the <strong>%s</strong> position.</p>
                <p style="margin:0 0 16px;">The recruiter will be in touch with next steps soon.</p>
                %s
                """.formatted(candidateName, jobTitle, EmailLayout.button("View application", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("You've been shortlisted for " + jobTitle, body));
    }

    public EmailContent interviewScheduled(String candidateName, String jobTitle) {
        String subject = "Interview scheduled for " + jobTitle;
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Your application for <strong>%s</strong> has moved to the interview stage.</p>
                <p style="margin:0 0 16px;">Please check your dashboard for interview details.</p>
                %s
                """.formatted(candidateName, jobTitle, EmailLayout.button("View interview details", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("Interview scheduled for " + jobTitle, body));
    }

    public EmailContent offerReleased(String candidateName, String jobTitle) {
        String subject = "Offer extended for " + jobTitle;
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Congratulations! You have received an offer for the <strong>%s</strong> position.</p>
                <p style="margin:0 0 16px;">Please log in to your dashboard to review the offer details.</p>
                %s
                """.formatted(candidateName, jobTitle, EmailLayout.button("Review my offer", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("You've received an offer for " + jobTitle, body));
    }

    public EmailContent applicationRejected(String candidateName, String jobTitle) {
        String subject = "Update on your application for " + jobTitle;
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Thank you for applying for the <strong>%s</strong> position.</p>
                <p style="margin:0 0 16px;">After careful review, the recruiter has decided not to move forward with your application at this time.
                We encourage you to keep exploring other opportunities on AI Job Portal.</p>
                %s
                """.formatted(candidateName, jobTitle, EmailLayout.button("Browse more jobs", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("An update on your application for " + jobTitle, body));
    }

    public EmailContent candidateHired(String candidateName, String jobTitle) {
        String subject = "Congratulations! You have been hired for " + jobTitle;
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Congratulations! You have been hired for the <strong>%s</strong> position.</p>
                <p style="margin:0 0 16px;">Welcome aboard — the recruiter will reach out with onboarding details.</p>
                %s
                """.formatted(candidateName, jobTitle, EmailLayout.button("Go to my dashboard", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("You've been hired for " + jobTitle, body));
    }

    public EmailContent resumeAnalysisReady(String candidateName, int atsScore) {
        String subject = "Your AI resume analysis is ready";
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Your resume has been analyzed by our AI engine. Your current ATS score is <strong>%d</strong>.</p>
                <p style="margin:0 0 16px;">Log in to your dashboard to view detailed suggestions for improving your resume.</p>
                %s
                """.formatted(candidateName, atsScore, EmailLayout.button("View my analysis", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("Your AI resume analysis is ready", body));
    }

    public EmailContent recommendationsReady(String candidateName, int recommendationCount) {
        String subject = "New AI job recommendations for you";
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Our AI engine has generated <strong>%d</strong> new job recommendations tailored to your profile.</p>
                %s
                """.formatted(candidateName, recommendationCount, EmailLayout.button("See my recommendations", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("New AI job recommendations are ready for you", body));
    }

    public EmailContent newJobPosted(String recruiterName, String jobTitle) {
        String subject = "Your job posting is now live: " + jobTitle;
        String body = """
                <p style="margin:0 0 16px;">Hi %s,</p>
                <p style="margin:0 0 16px;">Your job posting <strong>%s</strong> has been created successfully and is now visible to candidates.</p>
                %s
                """.formatted(recruiterName, jobTitle, EmailLayout.button("View job posting", dashboardUrl));
        return new EmailContent(subject, EmailLayout.render("Your job posting is now live", body));
    }
}
