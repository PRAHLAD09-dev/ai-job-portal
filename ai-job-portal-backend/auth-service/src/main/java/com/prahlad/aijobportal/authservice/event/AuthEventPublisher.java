package com.prahlad.aijobportal.authservice.event;

import com.prahlad.aijobportal.authservice.event.dto.PasswordResetRequestedEvent;
import com.prahlad.aijobportal.authservice.event.dto.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes Auth Service domain events to Kafka. Per DAY02_AUTH_SERVICE.md,
 * this service ONLY publishes events — it never implements a consumer, and
 * the Notification Service (which will eventually consume these topics) is
 * explicitly out of scope for this phase.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventPublisher {

    private static final String USER_REGISTERED_TOPIC = "user-registered";
    private static final String PASSWORD_RESET_REQUESTED_TOPIC = "password-reset-requested";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send(USER_REGISTERED_TOPIC, event.userId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish UserRegisteredEvent for userId={}", event.userId(), ex);
                    } else {
                        log.info("Published UserRegisteredEvent for userId={}", event.userId());
                    }
                });
    }

    public void publishPasswordResetRequested(PasswordResetRequestedEvent event) {
        kafkaTemplate.send(PASSWORD_RESET_REQUESTED_TOPIC, event.userId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish PasswordResetRequestedEvent for userId={}", event.userId(), ex);
                    } else {
                        log.info("Published PasswordResetRequestedEvent for userId={}", event.userId());
                    }
                });
    }
}
