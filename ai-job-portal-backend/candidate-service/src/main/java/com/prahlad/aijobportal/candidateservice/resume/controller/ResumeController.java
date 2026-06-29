package com.prahlad.aijobportal.candidateservice.resume.controller;

import com.prahlad.aijobportal.candidateservice.resume.dto.response.ResumeResponse;
import com.prahlad.aijobportal.candidateservice.resume.service.ResumeService;
import com.prahlad.aijobportal.candidateservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/candidate/resumes")
@RequiredArgsConstructor
@Tag(name = "Candidate Resumes", description = "Resume upload, versioning, and management")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a new resume for the authenticated candidate")
    public ResponseEntity<ApiResponse<ResumeResponse>> upload(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.upload(principal.userId(), file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all resume versions for the authenticated candidate")
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        List<ResumeResponse> response = resumeService.getAll(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping(value = "/{resumeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Replace an existing resume file, keeping the same resume record")
    public ResponseEntity<ApiResponse<ResumeResponse>> replace(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID resumeId,
            @RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.replace(principal.userId(), resumeId, file);
        return ResponseEntity.ok(ApiResponse.success("Resume replaced successfully", response));
    }

    @DeleteMapping("/{resumeId}")
    @Operation(summary = "Delete a resume owned by the authenticated candidate")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID resumeId) {
        resumeService.delete(principal.userId(), resumeId);
        return ResponseEntity.ok(ApiResponse.success("Resume deleted successfully", null));
    }
}
