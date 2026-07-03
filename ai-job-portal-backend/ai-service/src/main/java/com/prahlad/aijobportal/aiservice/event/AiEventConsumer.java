package com.prahlad.aijobportal.aiservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prahlad.aijobportal.aiservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.aiservice.event.consumed.ApplicationCreatedEvent;
import com.prahlad.aijobportal.aiservice.event.consumed.CandidateProfileUpdatedEvent;
import com.prahlad.aijobportal.aiservice.event.consumed.JobCreatedEvent;
import com.prahlad.aijobportal.aiservice.event.consumed.ResumeUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes domain events from other services, per
 * DAY07_AI_SERVICE.md's "Kafka Consumers" section. Payloads are read
 * as raw JSON strings (rather than {@code JsonDeserializer<T>}) and
 * parsed manually with a plain {@link ObjectMapper}, because Spring
 * Kafka's default JSON deserialization relies on a {@code __TypeId__}
 * header carrying the PRODUCER's fully-qualified class name — a class
 * that does not exist on this service's classpath. Reading as a string
 * and deserializing into this service's own locally-declared mirror
 * DTO sidesteps that entirely and keeps services decoupled.
 *
 * Each handler's job is narrow and defensible: invalidate the AI
 * caches that the event makes stale, rather than eagerly regenerating
 * AI content on every upstream write (which would be expensive and
 * mostly wasted, since not every stale cache entry is ever re-read).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiEventConsumer {

    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

    @KafkaListener(topics = "application-created", groupId = "ai-service")
    public void onApplicationCreated(String payload) {
        ApplicationCreatedEvent event = parse(payload, ApplicationCreatedEvent.class);
        if (event == null) {
            return;
        }
        log.info("Consumed ApplicationCreatedEvent for jobId={}, candidateId={}", event.jobId(), event.candidateId());
        evict(RedisCacheConfig.CANDIDATE_RECOMMENDATIONS_CACHE, event.jobId());
    }

    @KafkaListener(topics = "job-created", groupId = "ai-service")
    public void onJobCreated(String payload) {
        JobCreatedEvent event = parse(payload, JobCreatedEvent.class);
        if (event == null) {
            return;
        }
        log.info("Consumed JobCreatedEvent for jobId={}", event.jobId());
        clearCache(RedisCacheConfig.JOB_RECOMMENDATIONS_CACHE);
    }

    @KafkaListener(topics = "resume-uploaded", groupId = "ai-service")
    public void onResumeUploaded(String payload) {
        ResumeUploadedEvent event = parse(payload, ResumeUploadedEvent.class);
        if (event == null) {
            return;
        }
        log.info("Consumed ResumeUploadedEvent for candidateId={}", event.candidateId());
        evict(RedisCacheConfig.RESUME_ANALYSIS_CACHE, event.candidateId());
        evict(RedisCacheConfig.JOB_RECOMMENDATIONS_CACHE, event.candidateId());
    }

    @KafkaListener(topics = "candidate-profile-updated", groupId = "ai-service")
    public void onCandidateProfileUpdated(String payload) {
        CandidateProfileUpdatedEvent event = parse(payload, CandidateProfileUpdatedEvent.class);
        if (event == null) {
            return;
        }
        log.info("Consumed CandidateProfileUpdatedEvent for candidateId={}", event.candidateId());
        evict(RedisCacheConfig.JOB_RECOMMENDATIONS_CACHE, event.candidateId());
    }

    private <T> T parse(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (Exception ex) {
            log.error("Failed to parse Kafka payload as {}: {}", type.getSimpleName(), payload, ex);
            return null;
        }
    }

    private void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    private void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
