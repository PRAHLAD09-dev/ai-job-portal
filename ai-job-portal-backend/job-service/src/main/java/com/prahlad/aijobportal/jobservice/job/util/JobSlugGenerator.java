package com.prahlad.aijobportal.jobservice.job.util;

import com.prahlad.aijobportal.jobservice.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Generates a URL-safe, unique slug for a {@code Job} from its title
 * and company name, used as the public listing identifier
 * ({@code GET /jobs/slug/{slug}}). Appends a numeric suffix on
 * collision rather than failing the request.
 */
@Component
@RequiredArgsConstructor
public class JobSlugGenerator {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_HYPHENS = Pattern.compile("^-+|-+$");

    private final JobRepository jobRepository;

    public String generateUniqueSlug(String title, String companyName) {
        String base = normalize(title + "-" + companyName);
        String candidate = base;
        int suffix = 1;

        while (jobRepository.existsBySlug(candidate)) {
            suffix++;
            candidate = base + "-" + suffix;
        }

        return candidate;
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase();
        normalized = NON_ALPHANUMERIC.matcher(normalized).replaceAll("-");
        normalized = EDGE_HYPHENS.matcher(normalized).replaceAll("");
        return normalized.isBlank() ? "job" : normalized;
    }
}
