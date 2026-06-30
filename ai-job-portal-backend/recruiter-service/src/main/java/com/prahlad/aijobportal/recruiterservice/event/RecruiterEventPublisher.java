package com.prahlad.aijobportal.recruiterservice.event;

import com.prahlad.aijobportal.recruiterservice.event.dto.CompanyCreatedEvent;
import com.prahlad.aijobportal.recruiterservice.event.dto.CompanyUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes Recruiter Service domain events to Kafka. Per
 * DAY04_RECRUITER_SERVICE.md, this service ONLY publishes events — it
 * never implements a consumer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecruiterEventPublisher {

    private static final String COMPANY_CREATED_TOPIC = "company-created";
    private static final String COMPANY_UPDATED_TOPIC = "company-updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCompanyCreated(CompanyCreatedEvent event) {
        kafkaTemplate.send(COMPANY_CREATED_TOPIC, event.companyId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish CompanyCreatedEvent for companyId={}", event.companyId(), ex);
                    } else {
                        log.info("Published CompanyCreatedEvent for companyId={}", event.companyId());
                    }
                });
    }

    public void publishCompanyUpdated(CompanyUpdatedEvent event) {
        kafkaTemplate.send(COMPANY_UPDATED_TOPIC, event.companyId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish CompanyUpdatedEvent for companyId={}", event.companyId(), ex);
                    } else {
                        log.info("Published CompanyUpdatedEvent for companyId={}", event.companyId());
                    }
                });
    }
}
