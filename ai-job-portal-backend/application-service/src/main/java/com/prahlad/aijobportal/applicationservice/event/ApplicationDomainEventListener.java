package com.prahlad.aijobportal.applicationservice.event;

import com.prahlad.aijobportal.applicationservice.event.dto.ApplicationCreatedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.ApplicationStatusChangedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateHiredEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateRejectedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateShortlistedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges "an application lifecycle change happened" (created / status
 * changed / shortlisted / rejected / hired) to "actually publish it to
 * Kafka" - but only once the enclosing DB transaction commits. Same
 * rationale and pattern as the equivalent listeners already added to
 * Auth, Candidate, Recruiter, and Job services.
 *
 * <p><b>Naming note:</b> this class depends on
 * {@code com.prahlad.aijobportal.applicationservice.event.ApplicationEventPublisher}
 * (this service's own Kafka domain publisher) - NOT
 * {@code org.springframework.context.ApplicationEventPublisher} (Spring's
 * generic event bus, used by the service classes to raise these events
 * in the first place). Both share the simple name
 * "ApplicationEventPublisher"; only one is imported/used in any given
 * file to avoid ambiguity - this file imports the Kafka one (same
 * package, no import statement needed), the service classes import
 * Spring's.
 */
@Component
@RequiredArgsConstructor
public class ApplicationDomainEventListener {

    private final ApplicationEventPublisher applicationEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationCreated(ApplicationCreatedEvent event) {
        applicationEventPublisher.publishApplicationCreated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        applicationEventPublisher.publishApplicationStatusChanged(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCandidateShortlisted(CandidateShortlistedEvent event) {
        applicationEventPublisher.publishCandidateShortlisted(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCandidateRejected(CandidateRejectedEvent event) {
        applicationEventPublisher.publishCandidateRejected(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCandidateHired(CandidateHiredEvent event) {
        applicationEventPublisher.publishCandidateHired(event);
    }
}
