package com.prahlad.aijobportal.authservice.event;

import com.prahlad.aijobportal.authservice.event.dto.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges {@link UserRegisteredEvent} from "a domain event was raised
 * during {@code register()}" to "actually publish it to Kafka" - but only
 * once the database transaction that created the user has committed.
 *
 * <p>Previously, {@code AuthServiceImpl.register()} called
 * {@code AuthEventPublisher.publishUserRegistered(...)} directly, inside
 * the {@code @Transactional} method. Since {@code KafkaTemplate.send()} is
 * asynchronous, the message could reach Kafka before the surrounding
 * transaction committed (or even if it later rolled back), and a
 * transient Kafka outage would silently drop the event with no compensating
 * mechanism. Routing the same event through Spring's transaction-bound
 * event listener guarantees Kafka is only touched after the user row is
 * durably committed - if the transaction rolls back, this listener never
 * runs at all.
 */
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final AuthEventPublisher authEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        authEventPublisher.publishUserRegistered(event);
    }
}
