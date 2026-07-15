package com.prahlad.aijobportal.aiservice.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Shared helper used by every AI feature service to get a structured
 * (JSON) response out of the LLM: appends a strict "respond with JSON
 * only" instruction to the caller's prompt, invokes {@link GeminiClient},
 * strips markdown code fences the model sometimes wraps its output in
 * despite instructions, and deserializes into the caller's target
 * record type. Centralizing this means every feature service
 * (resume analysis, recommendations, interview questions, ...) writes
 * only its own prompt template and target DTO — never JSON-extraction
 * boilerplate.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiStructuredResponseService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public <T> T generateStructured(String prompt, Class<T> targetType) {
        String fullPrompt = prompt + """


                Respond with a single valid JSON object only. Do not include markdown \
                code fences, explanations, or any text outside the JSON object.""";

        String rawText;
        try {
            rawText = geminiClient.generateText(fullPrompt).join();
        } catch (Exception ex) {
            throw new AiGenerationException("AI generation failed", ex);
        }

        String json = stripCodeFences(rawText);

        try {
            return objectMapper.readValue(json, targetType);
        } catch (Exception ex) {
            log.error("Failed to parse AI response as {}. Raw response: {}", targetType.getSimpleName(), rawText, ex);
            throw new AiGenerationException("The AI provider returned a response in an unexpected format");
        }
    }

    /**
     * Handles both the normal case (a matched opening/closing pair of
     * fences) and a truncated response that only has the opening
     * fence - which would otherwise fall through and hand Jackson text
     * that still starts with a "```json" marker, producing a
     * misleading "unexpected character '`'" error instead of a clear
     * "invalid/incomplete JSON" one.
     */
    private String stripCodeFences(String text) {
        String trimmed = text.strip();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline == -1) {
                return trimmed;
            }
            int lastFence = trimmed.lastIndexOf("```");
            if (lastFence > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastFence).strip();
            }
            // No closing fence found (response was likely truncated) -
            // still strip the opening marker so the caller's JSON
            // parse failure clearly reflects "incomplete JSON", not
            // "there's a stray backtick here".
            return trimmed.substring(firstNewline + 1).strip();
        }
        return trimmed;
    }
}
