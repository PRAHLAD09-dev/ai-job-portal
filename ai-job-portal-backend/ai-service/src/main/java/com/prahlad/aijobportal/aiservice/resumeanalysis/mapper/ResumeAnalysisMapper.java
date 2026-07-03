package com.prahlad.aijobportal.aiservice.resumeanalysis.mapper;

import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ResumeAnalysisResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts between the persisted {@link ResumeAnalysis} entity and the
 * public {@link ResumeAnalysisResponse}. List fields are stored on the
 * entity as a single newline-delimited TEXT column (see entity Javadoc)
 * and split back into a {@code List<String>} here.
 */
@Mapper(componentModel = "spring")
public interface ResumeAnalysisMapper {

    String LIST_DELIMITER = "\n";

    ResumeAnalysisResponse toResponse(ResumeAnalysis entity);

    default List<String> toList(String delimited) {
        if (delimited == null || delimited.isBlank()) {
            return List.of();
        }
        return Arrays.stream(delimited.split(LIST_DELIMITER))
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    default String toDelimited(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return String.join(LIST_DELIMITER, items);
    }
}
