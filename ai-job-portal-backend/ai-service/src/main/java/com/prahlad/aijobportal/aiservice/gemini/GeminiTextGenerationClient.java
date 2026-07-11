package com.prahlad.aijobportal.aiservice.gemini;

import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.gemini.dto.GeminiGenerateContentRequest;
import com.prahlad.aijobportal.aiservice.gemini.dto.GeminiGenerateContentResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * The only class in this service that knows Gemini's REST contract.
 * Calls {@code POST /v1beta/models/{model}:generateContent} directly
 * (rather than pulling in a heavier SDK), guarded by the full
 * Resilience4j stack called for in DAY07_AI_SERVICE.md: Circuit
 * Breaker, Retry, Bulkhead, and TimeLimiter. TimeLimiter requires a
 * {@link CompletableFuture}-returning method, which is also why
 * {@link GeminiClient} is asynchronous even though every caller in
 * this service ultimately blocks on the result (DECISIONS.md specifies
 * "Synchronous AI Requests" from the caller's point of view).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiTextGenerationClient implements GeminiClient {

    private static final String RESILIENCE_INSTANCE = "geminiClient";

    private final WebClient geminiWebClient;
    private final GeminiProperties geminiProperties;

    @Override
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "generateTextFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    @Bulkhead(name = RESILIENCE_INSTANCE)
    @TimeLimiter(name = RESILIENCE_INSTANCE)
    public CompletableFuture<String> generateText(String prompt) {
        GeminiGenerateContentRequest request = new GeminiGenerateContentRequest(
                List.of(new GeminiGenerateContentRequest.Content(
                        List.of(new GeminiGenerateContentRequest.Part(prompt)))),
                new GeminiGenerateContentRequest.GenerationConfig(
                        geminiProperties.getTemperature(), geminiProperties.getMaxOutputTokens())
        );

        String uri = "/models/%s:generateContent?key=%s".formatted(geminiProperties.getModel(), geminiProperties.getApiKey());

        return geminiWebClient.post()
                .uri(geminiProperties.getBaseUrl() + uri)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiGenerateContentResponse.class)
                .map(this::extractText)
                .toFuture();
    }

    @SuppressWarnings("unused")
    private CompletableFuture<String> generateTextFallback(String prompt, Throwable throwable) {
        logGeminiFailureSafely(throwable);
        return CompletableFuture.failedFuture(
                new AiGenerationException("The AI service is temporarily unavailable. Please try again shortly.", throwable));
    }

    /**
     * The Gemini API key is passed in the request URL's {@code ?key=}
     * query parameter (Google's required auth mechanism for this API -
     * there's no header-based alternative for the public Generative
     * Language API). {@link WebClientResponseException} and
     * {@link WebClientRequestException} both include the full outbound
     * request URI - key included - in their own {@code getMessage()}/
     * {@code toString()}. Logging either of those directly (or logging
     * {@code throwable} itself, which SLF4J renders via that same
     * {@code toString()} as the first line of the stack trace) would
     * write the live API key to this service's own logs in plaintext on
     * every Gemini failure. This method logs only pre-approved, safe
     * fields - HTTP status code, exception type - and never the
     * exception's message or the request URI/endpoint itself.
     */
    private void logGeminiFailureSafely(Throwable throwable) {
        Throwable cause = unwrapCompletionException(throwable);

        if (cause instanceof WebClientResponseException responseException) {
            log.error("Gemini generateContent call failed: HTTP {} {} from the Gemini API",
                    responseException.getStatusCode().value(), responseException.getStatusText());
        } else if (cause instanceof WebClientRequestException) {
            log.error("Gemini generateContent call failed: unable to reach the Gemini API ({})",
                    cause.getClass().getSimpleName());
        } else {
            // Other failure types (timeout, circuit breaker open, JSON
            // parsing, etc.) don't carry the request URI in their
            // message, so it's safe to log them with their full context.
            log.error("Gemini generateContent call failed after retries/circuit breaker: {}",
                    cause.getClass().getSimpleName(), cause);
        }
    }

    private Throwable unwrapCompletionException(Throwable throwable) {
        if (throwable instanceof CompletionException && throwable.getCause() != null) {
            return throwable.getCause();
        }
        return throwable;
    }

    private String extractText(GeminiGenerateContentResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new AiGenerationException("The AI provider returned an empty response");
        }

        GeminiGenerateContentResponse.Content content = response.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            throw new AiGenerationException("The AI provider returned no content");
        }

        StringBuilder text = new StringBuilder();
        for (GeminiGenerateContentResponse.Part part : content.parts()) {
            if (part.text() != null) {
                text.append(part.text());
            }
        }

        if (text.isEmpty()) {
            throw new AiGenerationException("The AI provider returned no text content");
        }

        return text.toString();
    }
}
