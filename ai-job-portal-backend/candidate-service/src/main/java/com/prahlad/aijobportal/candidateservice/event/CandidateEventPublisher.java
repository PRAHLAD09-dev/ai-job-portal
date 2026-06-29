package com.prahlad.aijobportal.candidateservice.event;

import com.prahlad.aijobportal.candidateservice.event.dto.CandidateProfileUpdatedEvent;
import com.prahlad.aijobportal.candidateservice.event.dto.ResumeUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes Candidate Service domain events to Kafka. Per
 * DAY03_CANDIDATE_SERVICE.md, this service ONLY publishes events — it
 * never implements a consumer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CandidateEventPublisher {

    private static final String RESUME_UPLOADED_TOPIC = "resume-uploaded";
    private static final String CANDIDATE_PROFILE_UPDATED_TOPIC = "candidate-profile-updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishResumeUploaded(ResumeUploadedEvent event) {
        kafkaTemplate.send(RESUME_UPLOADED_TOPIC, event.candidateId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ResumeUploadedEvent for candidateId={}", event.candidateId(), ex);
                    } else {
                        log.info("Published ResumeUploadedEvent for candidateId={}", event.candidateId());
                    }
                });
    }

    public void publishCandidateProfileUpdated(CandidateProfileUpdatedEvent event) {
        kafkaTemplate.send(CANDIDATE_PROFILE_UPDATED_TOPIC, event.candidateId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish CandidateProfileUpdatedEvent for candidateId={}", event.candidateId(), ex);
                    } else {
                        log.info("Published CandidateProfileUpdatedEvent for candidateId={}", event.candidateId());
                    }
                });
    }
}
