package com.prahlad.aijobportal.candidateservice.resume.dto.response;

import com.prahlad.aijobportal.candidateservice.resume.enums.ResumeStatus;

import java.time.Instant;
import java.util.UUID;

public record ResumeResponse(
        UUID id,
        String fileName,
        String fileUrl,
        String fileFormat,
        long fileSizeBytes,
        int versionNumber,
        ResumeStatus status,
        Instant createdAt
) {
}
