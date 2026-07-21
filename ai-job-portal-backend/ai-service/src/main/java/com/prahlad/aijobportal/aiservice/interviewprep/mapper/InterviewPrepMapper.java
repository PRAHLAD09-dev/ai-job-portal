package com.prahlad.aijobportal.aiservice.interviewprep.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.InterviewPrepAiResult;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.InterviewPrepQuestionSetResponse;
import com.prahlad.aijobportal.aiservice.interviewprep.entity.InterviewPrepQuestionSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts between the persisted {@link InterviewPrepQuestionSet}
 * entity - which stores the AI-generated questions as a single flat
 * JSON array, each item tagged with its topic - and the public,
 * topic-grouped {@link InterviewPrepQuestionSetResponse}. Grouping by
 * topic here (rather than storing pre-grouped) keeps the persisted
 * shape simple and matches the AI result's own shape.
 *
 * <p>A plain {@code @Component} rather than MapStruct: the entity-to-
 * response conversion here isn't a field-for-field mapping (it involves
 * JSON parsing and a groupingBy), so a generated mapper would end up
 * mostly hand-written anyway.
 */
@Component
@RequiredArgsConstructor
public class InterviewPrepMapper {

    private static final String LIST_DELIMITER = "\n";

    private final ObjectMapper objectMapper;

    /** Same delimited-TEXT-column convention {@code ResumeAnalysisMapper} uses for string lists. */
    public String toDelimited(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return String.join(LIST_DELIMITER, items);
    }

    public List<String> toList(String delimited) {
        if (delimited == null || delimited.isBlank()) {
            return List.of();
        }
        return Arrays.stream(delimited.split(LIST_DELIMITER))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    public String toQuestionsJson(List<InterviewPrepAiResult.Item> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception ex) {
            throw new AiGenerationException("Failed to save the generated interview questions");
        }
    }

    public List<InterviewPrepAiResult.Item> fromQuestionsJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<InterviewPrepAiResult.Item>>() {
            });
        } catch (Exception ex) {
            throw new AiGenerationException("Failed to read the stored interview questions");
        }
    }

    public InterviewPrepQuestionSetResponse toResponse(InterviewPrepQuestionSet entity) {
        List<InterviewPrepAiResult.Item> items = fromQuestionsJson(entity.getQuestionsJson());

        Map<String, List<String>> grouped = items.stream().collect(Collectors.groupingBy(
                InterviewPrepAiResult.Item::topic,
                LinkedHashMap::new,
                Collectors.mapping(InterviewPrepAiResult.Item::question, Collectors.toList())));

        List<InterviewPrepQuestionSetResponse.TopicQuestionsResponse> sections = grouped.entrySet().stream()
                .map(entry -> new InterviewPrepQuestionSetResponse.TopicQuestionsResponse(entry.getKey(), entry.getValue()))
                .toList();

        return new InterviewPrepQuestionSetResponse(
                entity.getId(),
                toList(entity.getSelectedTopics()),
                entity.getDifficulty().name(),
                entity.getQuestionType().name(),
                items.size(),
                sections,
                entity.getCreatedAt());
    }
}
