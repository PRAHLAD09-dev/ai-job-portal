package com.prahlad.aijobportal.notificationservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prahlad.aijobportal.notificationservice.email.EmailContent;
import com.prahlad.aijobportal.notificationservice.email.EmailTemplateBuilder;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.ApplicationCreatedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.ApplicationStatus;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.ApplicationStatusChangedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.CandidateHiredEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.CandidateRejectedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.CandidateShortlistedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.JobCreatedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.JobUpdatedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.PasswordResetRequestedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.RecommendationGeneratedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.ResumeAnalyzedEvent;
import com.prahlad.aijobportal.notificationservice.event.dto.consumed.UserRegisteredEvent;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationType;
import com.prahlad.aijobportal.notificationservice.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes every domain event this service reacts to, per
 * DAY08_NOTIFICATION_SERVICE.md "Events (Kafka Consumers)" section, and
 * turns each into an in-app notification and/or e-mail via
 * NotificationService.dispatch.
 *
 * Payloads are deserialized manually with a plain ObjectMapper (rather
 * than a JsonDeserializer type-mapped to the producer's exact class),
 * mirroring the established convention in AiEventConsumer — this
 * service never depends on another service's DTO classes across the
 * module boundary.
 *
 * Two events described in DAY08 do not map to a distinct Kafka topic in
 * the existing codebase and are intentionally not separately consumed
 * here:
 *  - "Email Verification": Auth Service sends this transactional e-mail
 *    itself (SmtpEmailService), by design, since only Auth Service holds
 *    the verification token.
 *  - "Interview Scheduled" / "Offer Released": these are
 *    ApplicationStatus values (INTERVIEW, OFFERED), not separate topics —
 *    both are handled inside handleApplicationStatusChanged below.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final EmailTemplateBuilder emailTemplateBuilder;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-registered", groupId = "notification-service-group")
    public void handleUserRegistered(String payload) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(payload, UserRegisteredEvent.class);
            String title = "Welcome to AI Job Portal";
            String message = "Hi " + event.firstName() + ", your account has been created successfully.";
            EmailContent email = emailTemplateBuilder.welcome(event.firstName());

            notificationService.dispatch(event.userId(), NotificationType.SYSTEM, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process user-registered event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "password-reset-requested", groupId = "notification-service-group")
    public void handlePasswordResetRequested(String payload) {
        try {
            PasswordResetRequestedEvent event = objectMapper.readValue(payload, PasswordResetRequestedEvent.class);
            String title = "Password reset requested";
            String message = "A password reset was requested for your account. If this was not you, please secure your account.";
            EmailContent email = emailTemplateBuilder.passwordResetRequested(event.firstName());

            notificationService.dispatch(event.userId(), NotificationType.SYSTEM, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process password-reset-requested event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "application-created", groupId = "notification-service-group")
    public void handleApplicationCreated(String payload) {
        try {
            ApplicationCreatedEvent event = objectMapper.readValue(payload, ApplicationCreatedEvent.class);
            String title = "Application submitted";
            String message = "Your application for \"" + event.jobTitle() + "\" has been submitted.";
            EmailContent email = emailTemplateBuilder.applicationSubmitted("there", event.jobTitle());

            notificationService.dispatch(event.candidateUserId(), NotificationType.APPLICATION, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process application-created event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "application-status-changed", groupId = "notification-service-group")
    public void handleApplicationStatusChanged(String payload) {
        try {
            ApplicationStatusChangedEvent event = objectMapper.readValue(payload, ApplicationStatusChangedEvent.class);
            ApplicationStatus newStatus = event.newStatus();

            NotificationType type = switch (newStatus) {
                case INTERVIEW -> NotificationType.INTERVIEW;
                case OFFERED -> NotificationType.OFFER;
                default -> NotificationType.APPLICATION;
            };

            String title = "Application status updated";
            String message = "Your application status changed to " + newStatus.name() + ".";

            EmailContent email = switch (newStatus) {
                case INTERVIEW -> emailTemplateBuilder.interviewScheduled("there", "your application");
                case OFFERED -> emailTemplateBuilder.offerReleased("there", "your application");
                default -> null;
            };

            notificationService.dispatch(event.candidateUserId(), type, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process application-status-changed event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "candidate-shortlisted", groupId = "notification-service-group")
    public void handleCandidateShortlisted(String payload) {
        try {
            CandidateShortlistedEvent event = objectMapper.readValue(payload, CandidateShortlistedEvent.class);
            String title = "You have been shortlisted";
            String message = "You have been shortlisted for \"" + event.jobTitle() + "\".";
            EmailContent email = emailTemplateBuilder.applicationShortlisted("there", event.jobTitle());

            notificationService.dispatch(event.candidateUserId(), NotificationType.APPLICATION, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process candidate-shortlisted event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "candidate-rejected", groupId = "notification-service-group")
    public void handleCandidateRejected(String payload) {
        try {
            CandidateRejectedEvent event = objectMapper.readValue(payload, CandidateRejectedEvent.class);
            String title = "Application update";
            String message = "Your application for \"" + event.jobTitle() + "\" was not selected to move forward.";
            EmailContent email = emailTemplateBuilder.applicationRejected("there", event.jobTitle());

            notificationService.dispatch(event.candidateUserId(), NotificationType.APPLICATION, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process candidate-rejected event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "candidate-hired", groupId = "notification-service-group")
    public void handleCandidateHired(String payload) {
        try {
            CandidateHiredEvent event = objectMapper.readValue(payload, CandidateHiredEvent.class);
            String title = "Congratulations, you are hired!";
            String message = "You have been hired for \"" + event.jobTitle() + "\".";
            EmailContent email = emailTemplateBuilder.candidateHired("there", event.jobTitle());

            notificationService.dispatch(event.candidateUserId(), NotificationType.OFFER, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process candidate-hired event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "job-created", groupId = "notification-service-group")
    public void handleJobCreated(String payload) {
        try {
            JobCreatedEvent event = objectMapper.readValue(payload, JobCreatedEvent.class);
            String title = "Job posted successfully";
            String message = "Your job \"" + event.title() + "\" is now live.";
            EmailContent email = emailTemplateBuilder.newJobPosted("there", event.title());

            notificationService.dispatch(event.recruiterUserId(), NotificationType.JOB, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process job-created event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "job-updated", groupId = "notification-service-group")
    public void handleJobUpdated(String payload) {
        try {
            JobUpdatedEvent event = objectMapper.readValue(payload, JobUpdatedEvent.class);
            // JobUpdatedEvent does not carry recruiterUserId (only companyId); without a
            // Recruiter Service lookup this cannot be routed to a specific recruiter
            // inbox, so it is logged only. See DAY08 implementation notes.
            log.info("Job updated: jobId={}, title={}, companyId={}", event.jobId(), event.title(), event.companyId());
        } catch (Exception ex) {
            log.error("Failed to process job-updated event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "resume-analyzed", groupId = "notification-service-group")
    public void handleResumeAnalyzed(String payload) {
        try {
            ResumeAnalyzedEvent event = objectMapper.readValue(payload, ResumeAnalyzedEvent.class);
            String title = "Your AI resume analysis is ready";
            String message = "Your resume ATS score is " + event.atsScore() + ".";
            EmailContent email = emailTemplateBuilder.resumeAnalysisReady("there", event.atsScore());

            notificationService.dispatch(event.candidateUserId(), NotificationType.AI, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process resume-analyzed event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "recommendation-generated", groupId = "notification-service-group")
    public void handleRecommendationGenerated(String payload) {
        try {
            RecommendationGeneratedEvent event = objectMapper.readValue(payload, RecommendationGeneratedEvent.class);
            String title = "New AI job recommendations";
            String message = event.recommendationCount() + " new job recommendations are ready for you.";
            EmailContent email = emailTemplateBuilder.recommendationsReady("there", event.recommendationCount());

            notificationService.dispatch(event.candidateUserId(), NotificationType.AI, title, message, email);
        } catch (Exception ex) {
            log.error("Failed to process recommendation-generated event: {}", payload, ex);
        }
    }
}
