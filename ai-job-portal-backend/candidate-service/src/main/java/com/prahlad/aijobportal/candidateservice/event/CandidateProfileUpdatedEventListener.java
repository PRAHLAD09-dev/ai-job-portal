package com.prahlad.aijobportal.candidateservice.event;

import com.prahlad.aijobportal.candidateservice.event.dto.CandidateProfileUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges "a profile-completion recalculation happened" to "actually
 * publish it to Kafka" - but only once the enclosing DB transaction has
 * committed. Previously, {@code ProfileCompletionService.recalculate()}
 * called {@code CandidateEventPublisher.publishCandidateProfileUpdated(...)}
 * directly inside the {@code @Transactional} method; since
 * {@code recalculate()} runs after nearly every profile/education/
 * experience/skill/resume mutation, that meant every such write risked
 * publishing an event for a change that later rolled back, or silently
 * losing the event if Kafka was briefly unreachable while the DB commit
 * still succeeded. Routing through Spring's transaction-bound event
 * listener (same pattern as Auth Service's UserRegisteredEventListener)
 * guarantees Kafka is only touched after the change is durably committed.
 */
@Component
@RequiredArgsConstructor
public class CandidateProfileUpdatedEventListener {

    private final CandidateEventPublisher candidateEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCandidateProfileUpdated(CandidateProfileUpdatedEvent event) {
        candidateEventPublisher.publishCandidateProfileUpdated(event);
    }
}
