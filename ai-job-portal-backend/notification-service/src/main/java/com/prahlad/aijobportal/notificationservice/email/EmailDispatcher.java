package com.prahlad.aijobportal.notificationservice.email;

import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.notificationservice.feign.AuthServiceClient;
import com.prahlad.aijobportal.notificationservice.feign.dto.UserSummaryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resolves a recipient's e-mail address via Auth Service and sends the
 * given e-mail content. Deliberately a separate Spring bean (rather than
 * a private method on NotificationServiceImpl) so the
 * {@link CircuitBreaker} / {@link Retry} annotations are honored — both
 * only intercept calls that pass through the Spring AOP proxy, which
 * self-invocation from within the same class would bypass.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailDispatcher {

    private final AuthServiceClient authServiceClient;
    private final EmailService emailService;

    @CircuitBreaker(name = "authService", fallbackMethod = "dispatchFallback")
    @Retry(name = "authService")
    public void dispatch(UUID userId, EmailContent emailContent) {
        ApiResponse<UserSummaryResponse> response = authServiceClient.getUserById(userId);
        UserSummaryResponse user = response.getData();

        if (user == null || user.email() == null) {
            log.warn("Could not resolve e-mail address for userId={}; skipping e-mail dispatch", userId);
            return;
        }

        try {
            emailService.send(user.email(), emailContent);
        } catch (EmailDeliveryException ex) {
            log.error("E-mail dispatch failed for userId={}", userId, ex);
        }
    }

    @SuppressWarnings("unused")
    private void dispatchFallback(UUID userId, EmailContent emailContent, Throwable throwable) {
        log.warn("Auth Service unavailable; skipping e-mail dispatch for userId={} ({})", userId, throwable.getMessage());
    }
}
