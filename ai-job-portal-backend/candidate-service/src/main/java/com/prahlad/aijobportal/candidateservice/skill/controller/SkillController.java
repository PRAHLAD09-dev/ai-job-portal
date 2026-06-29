package com.prahlad.aijobportal.candidateservice.skill.controller;

import com.prahlad.aijobportal.candidateservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.candidateservice.skill.dto.request.SkillRequest;
import com.prahlad.aijobportal.candidateservice.skill.dto.response.SkillResponse;
import com.prahlad.aijobportal.candidateservice.skill.service.SkillService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/candidate/skills")
@RequiredArgsConstructor
@Tag(name = "Candidate Skills", description = "CRUD operations on a candidate's skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @Operation(summary = "Add a skill to the authenticated candidate's profile")
    public ResponseEntity<ApiResponse<SkillResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.create(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill added successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all skills for the authenticated candidate")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        List<SkillResponse> response = skillService.getAll(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{skillId}")
    @Operation(summary = "Update a skill owned by the authenticated candidate")
    public ResponseEntity<ApiResponse<SkillResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID skillId,
            @Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.update(principal.userId(), skillId, request);
        return ResponseEntity.ok(ApiResponse.success("Skill updated successfully", response));
    }

    @DeleteMapping("/{skillId}")
    @Operation(summary = "Delete a skill owned by the authenticated candidate")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID skillId) {
        skillService.delete(principal.userId(), skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted successfully", null));
    }
}
