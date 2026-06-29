package com.prahlad.aijobportal.candidateservice.resume.service;

import com.prahlad.aijobportal.candidateservice.resume.dto.response.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ResumeService {

    ResumeResponse upload(UUID userId, MultipartFile file);

    List<ResumeResponse> getAll(UUID userId);

    ResumeResponse replace(UUID userId, UUID resumeId, MultipartFile file);

    void delete(UUID userId, UUID resumeId);
}
