package com.prahlad.aijobportal.recruiterservice.company.util;

import com.prahlad.aijobportal.recruiterservice.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Generates a URL-safe, unique slug for a {@code Company} from its
 * display name, used as the public profile identifier
 * ({@code GET /companies/{slug}/public}). Appends a numeric suffix on
 * collision rather than failing the request.
 */
@Component
@RequiredArgsConstructor
public class SlugGenerator {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_HYPHENS = Pattern.compile("^-+|-+$");

    private final CompanyRepository companyRepository;

    public String generateUniqueSlug(String companyName) {
        String base = normalize(companyName);
        String candidate = base;
        int suffix = 1;

        while (companyRepository.existsBySlug(candidate)) {
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
        return normalized.isBlank() ? "company" : normalized;
    }
}
