package com.prahlad.aijobportal.jobservice.event;

import com.prahlad.aijobportal.jobservice.event.dto.JobClosedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobCreatedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobDeletedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobPublishedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges "a job lifecycle change happened" (created/updated/deleted/
 * published/closed) to "actually publish it to Kafka" - but only once
 * the enclosing DB transaction commits. Same rationale and pattern as
 * Auth Service's UserRegisteredEventListener, Candidate Service's
 * CandidateProfileUpdatedEventListener/ResumeUploadedEventListener, and
 * Recruiter Service's CompanyEventListener: publishing directly inside a
 * {@code @Transactional} method risked publishing an event for a change
 * that later rolled back, or silently losing the event if Kafka was
 * briefly unreachable while the DB commit still succeeded.
 */
@Component
@RequiredArgsConstructor
public class JobEventListener {

    private final JobEventPublisher jobEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobCreated(JobCreatedEvent event) {
        jobEventPublisher.publishJobCreated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobUpdated(JobUpdatedEvent event) {
        jobEventPublisher.publishJobUpdated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobDeleted(JobDeletedEvent event) {
        jobEventPublisher.publishJobDeleted(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobPublished(JobPublishedEvent event) {
        jobEventPublisher.publishJobPublished(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobClosed(JobClosedEvent event) {
        jobEventPublisher.publishJobClosed(event);
    }
}
