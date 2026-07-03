package com.prahlad.aijobportal.aiservice.event;

import com.prahlad.aijobportal.aiservice.event.dto.ATSCompletedEvent;
import com.prahlad.aijobportal.aiservice.event.dto.CandidateRankedEvent;
import com.prahlad.aijobportal.aiservice.event.dto.RecommendationGeneratedEvent;
import com.prahlad.aijobportal.aiservice.event.dto.ResumeAnalyzedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Publishes AI Service domain events to Kafka, per
 * DAY07_AI_SERVICE.md's "Kafka Producers" section.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiEventPublisher {

    private static final String RESUME_ANALYZED_TOPIC = "resume-analyzed";
    private static final String CANDIDATE_RANKED_TOPIC = "candidate-ranked";
    private static final String RECOMMENDATION_GENERATED_TOPIC = "recommendation-generated";
    private static final String ATS_COMPLETED_TOPIC = "ats-completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishResumeAnalyzed(ResumeAnalyzedEvent event) {
        send(RESUME_ANALYZED_TOPIC, event.candidateId(), event, "ResumeAnalyzedEvent");
    }

    public void publishCandidateRanked(CandidateRankedEvent event) {
        send(CANDIDATE_RANKED_TOPIC, event.jobId(), event, "CandidateRankedEvent");
    }

    public void publishRecommendationGenerated(RecommendationGeneratedEvent event) {
        send(RECOMMENDATION_GENERATED_TOPIC, event.candidateId(), event, "RecommendationGeneratedEvent");
    }

    public void publishAtsCompleted(ATSCompletedEvent event) {
        send(ATS_COMPLETED_TOPIC, event.candidateId(), event, "ATSCompletedEvent");
    }

    private void send(String topic, UUID key, Object event, String eventName) {
        kafkaTemplate.send(topic, key.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} key={}", eventName, key, ex);
                    } else {
                        log.info("Published {} key={}", eventName, key);
                    }
                });
    }
}
