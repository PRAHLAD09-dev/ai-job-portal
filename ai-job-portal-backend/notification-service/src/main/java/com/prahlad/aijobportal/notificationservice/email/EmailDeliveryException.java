package com.prahlad.aijobportal.notificationservice.email;

/**
 * Thrown when SMTP delivery fails. Caught by the caller
 * (NotificationServiceImpl) so the Notification row can be persisted
 * with status FAILED instead of propagating and losing the Kafka
 * consumer offset commit.
 */
public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
