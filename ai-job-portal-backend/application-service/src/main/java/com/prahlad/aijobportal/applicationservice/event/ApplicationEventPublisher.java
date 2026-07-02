package com.prahlad.aijobportal.applicationservice.event;

import com.prahlad.aijobportal.applicationservice.event.dto.ApplicationCreatedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.ApplicationStatusChangedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateHiredEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateRejectedEvent;
import com.prahlad.aijobportal.applicationservice.event.dto.CandidateShortlistedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Publishes Application Service domain events to Kafka. Per
 * DAY06_APPLICATION_SERVICE.md, this service ONLY publishes events —
 * it never implements a consumer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventPublisher {

    private static final String APPLICATION_CREATED_TOPIC = "application-created";
    private static final String APPLICATION_STATUS_CHANGED_TOPIC = "application-status-changed";
    private static final String CANDIDATE_SHORTLISTED_TOPIC = "candidate-shortlisted";
    private static final String CANDIDATE_REJECTED_TOPIC = "candidate-rejected";
    private static final String CANDIDATE_HIRED_TOPIC = "candidate-hired";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishApplicationCreated(ApplicationCreatedEvent event) {
        send(APPLICATION_CREATED_TOPIC, event.applicationId(), event, "ApplicationCreatedEvent");
    }

    public void publishApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        send(APPLICATION_STATUS_CHANGED_TOPIC, event.applicationId(), event, "ApplicationStatusChangedEvent");
    }

    public void publishCandidateShortlisted(CandidateShortlistedEvent event) {
        send(CANDIDATE_SHORTLISTED_TOPIC, event.applicationId(), event, "CandidateShortlistedEvent");
    }

    public void publishCandidateRejected(CandidateRejectedEvent event) {
        send(CANDIDATE_REJECTED_TOPIC, event.applicationId(), event, "CandidateRejectedEvent");
    }

    public void publishCandidateHired(CandidateHiredEvent event) {
        send(CANDIDATE_HIRED_TOPIC, event.applicationId(), event, "CandidateHiredEvent");
    }

    private void send(String topic, UUID key, Object event, String eventName) {
        kafkaTemplate.send(topic, key.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} for applicationId={}", eventName, key, ex);
                    } else {
                        log.info("Published {} for applicationId={}", eventName, key);
                    }
                });
    }
}
