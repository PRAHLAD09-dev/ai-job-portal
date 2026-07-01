package com.prahlad.aijobportal.jobservice.event;

import com.prahlad.aijobportal.jobservice.event.dto.JobClosedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobCreatedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobDeletedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobPublishedEvent;
import com.prahlad.aijobportal.jobservice.event.dto.JobUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Publishes Job Service domain events to Kafka. Per
 * DAY05_JOB_SERVICE.md, this service ONLY publishes events — it never
 * implements a consumer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobEventPublisher {

    private static final String JOB_CREATED_TOPIC = "job-created";
    private static final String JOB_UPDATED_TOPIC = "job-updated";
    private static final String JOB_DELETED_TOPIC = "job-deleted";
    private static final String JOB_PUBLISHED_TOPIC = "job-published";
    private static final String JOB_CLOSED_TOPIC = "job-closed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishJobCreated(JobCreatedEvent event) {
        send(JOB_CREATED_TOPIC, event.jobId(), event, "JobCreatedEvent");
    }

    public void publishJobUpdated(JobUpdatedEvent event) {
        send(JOB_UPDATED_TOPIC, event.jobId(), event, "JobUpdatedEvent");
    }

    public void publishJobDeleted(JobDeletedEvent event) {
        send(JOB_DELETED_TOPIC, event.jobId(), event, "JobDeletedEvent");
    }

    public void publishJobPublished(JobPublishedEvent event) {
        send(JOB_PUBLISHED_TOPIC, event.jobId(), event, "JobPublishedEvent");
    }

    public void publishJobClosed(JobClosedEvent event) {
        send(JOB_CLOSED_TOPIC, event.jobId(), event, "JobClosedEvent");
    }

    private void send(String topic, UUID key, Object event, String eventName) {
        kafkaTemplate.send(topic, key.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} for jobId={}", eventName, key, ex);
                    } else {
                        log.info("Published {} for jobId={}", eventName, key);
                    }
                });
    }
}
