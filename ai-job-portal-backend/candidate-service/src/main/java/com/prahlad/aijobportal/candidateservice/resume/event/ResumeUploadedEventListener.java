package com.prahlad.aijobportal.candidateservice.resume.event;

import com.prahlad.aijobportal.candidateservice.event.CandidateEventPublisher;
import com.prahlad.aijobportal.candidateservice.event.dto.ResumeUploadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges "a resume was uploaded/replaced" to "actually publish it to
 * Kafka" - but only once the DB transaction has committed. Same
 * rationale and pattern as Auth Service's UserRegisteredEventListener
 * and this service's CandidateProfileUpdatedEventListener.
 */
@Component
@RequiredArgsConstructor
public class ResumeUploadedEventListener {

    private final CandidateEventPublisher candidateEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onResumeUploaded(ResumeUploadedEvent event) {
        candidateEventPublisher.publishResumeUploaded(event);
    }
}
