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

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        log.error("Gemini API call failed after retries/circuit breaker", throwable);
        return CompletableFuture.failedFuture(
                new AiGenerationException("The AI service is temporarily unavailable. Please try again shortly.", throwable));
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
