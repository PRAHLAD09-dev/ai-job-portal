package com.prahlad.aijobportal.candidateservice.resume.util;

import com.prahlad.aijobportal.candidateservice.resume.config.ResumeProperties;
import com.prahlad.aijobportal.candidateservice.resume.exception.InvalidResumeFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates an uploaded resume file against the configured size and
 * format constraints before it is forwarded to Cloudinary.
 */
@Component
@RequiredArgsConstructor
public class ResumeFileValidator {

    private final ResumeProperties resumeProperties;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidResumeFileException("Resume file must not be empty");
        }

        if (file.getSize() > resumeProperties.getMaxFileSizeBytes()) {
            throw new InvalidResumeFileException(
                    "Resume file exceeds the maximum allowed size of "
                            + (resumeProperties.getMaxFileSizeBytes() / (1024 * 1024)) + " MB");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(extension) || !resumeProperties.getAllowedFormats().contains(extension.toLowerCase())) {
            throw new InvalidResumeFileException(
                    "Unsupported resume file format. Allowed formats: " + resumeProperties.getAllowedFormats());
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
