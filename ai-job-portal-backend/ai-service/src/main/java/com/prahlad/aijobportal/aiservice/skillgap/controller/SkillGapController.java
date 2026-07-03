package com.prahlad.aijobportal.aiservice.skillgap.controller;

import com.prahlad.aijobportal.aiservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.aiservice.skillgap.dto.response.SkillGapResponse;
import com.prahlad.aijobportal.aiservice.skillgap.service.SkillGapService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/skills")
@RequiredArgsConstructor
@Tag(name = "AI Skill Gap", description = "Candidate skill gap analysis against the current job market")
public class SkillGapController {

    private final SkillGapService skillGapService;

    @GetMapping("/gap")
    @Operation(summary = "Analyze the authenticated candidate's skill gap against currently open jobs")
    public ResponseEntity<ApiResponse<SkillGapResponse>> getSkillGap(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken) {
        SkillGapResponse response = skillGapService.analyze(principal.userId(), bearerToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
