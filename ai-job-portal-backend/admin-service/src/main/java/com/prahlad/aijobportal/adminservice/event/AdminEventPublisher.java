package com.prahlad.aijobportal.adminservice.event;

import com.prahlad.aijobportal.adminservice.event.dto.CompanyVerifiedEvent;
import com.prahlad.aijobportal.adminservice.event.dto.JobRemovedEvent;
import com.prahlad.aijobportal.adminservice.event.dto.UserDisabledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes Admin Service domain events to Kafka. Per
 * DAY09_ADMIN_SERVICE.md, this service ONLY publishes events — it never
 * implements a consumer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminEventPublisher {

    private static final String USER_DISABLED_TOPIC = "user-disabled";
    private static final String COMPANY_VERIFIED_TOPIC = "company-verified";
    private static final String JOB_REMOVED_TOPIC = "job-removed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserDisabled(UserDisabledEvent event) {
        kafkaTemplate.send(USER_DISABLED_TOPIC, event.userId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish UserDisabledEvent for userId={}", event.userId(), ex);
                    } else {
                        log.info("Published UserDisabledEvent for userId={}", event.userId());
                    }
                });
    }

    public void publishCompanyVerified(CompanyVerifiedEvent event) {
        kafkaTemplate.send(COMPANY_VERIFIED_TOPIC, event.companyId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish CompanyVerifiedEvent for companyId={}", event.companyId(), ex);
                    } else {
                        log.info("Published CompanyVerifiedEvent for companyId={}", event.companyId());
                    }
                });
    }

    public void publishJobRemoved(JobRemovedEvent event) {
        kafkaTemplate.send(JOB_REMOVED_TOPIC, event.jobId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish JobRemovedEvent for jobId={}", event.jobId(), ex);
                    } else {
                        log.info("Published JobRemovedEvent for jobId={}", event.jobId());
                    }
                });
    }
}
