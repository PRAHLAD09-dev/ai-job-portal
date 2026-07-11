package com.prahlad.aijobportal.recruiterservice.event;

import com.prahlad.aijobportal.recruiterservice.event.dto.CompanyCreatedEvent;
import com.prahlad.aijobportal.recruiterservice.event.dto.CompanyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges "a company was created/updated" to "actually publish it to
 * Kafka" - but only once the enclosing DB transaction commits. Same
 * rationale and pattern as Auth Service's UserRegisteredEventListener
 * and Candidate Service's CandidateProfileUpdatedEventListener /
 * ResumeUploadedEventListener: publishing directly inside a
 * {@code @Transactional} method risked publishing an event for a change
 * that later rolled back, or silently losing the event if Kafka was
 * briefly unreachable while the DB commit still succeeded.
 */
@Component
@RequiredArgsConstructor
public class CompanyEventListener {

    private final RecruiterEventPublisher recruiterEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompanyCreated(CompanyCreatedEvent event) {
        recruiterEventPublisher.publishCompanyCreated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompanyUpdated(CompanyUpdatedEvent event) {
        recruiterEventPublisher.publishCompanyUpdated(event);
    }
}
