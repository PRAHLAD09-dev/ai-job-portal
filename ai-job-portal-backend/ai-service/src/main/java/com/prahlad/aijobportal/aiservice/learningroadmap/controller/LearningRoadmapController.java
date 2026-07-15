package com.prahlad.aijobportal.aiservice.learningroadmap.controller;

import com.prahlad.aijobportal.aiservice.learningroadmap.dto.response.LearningRoadmapResponse;
import com.prahlad.aijobportal.aiservice.learningroadmap.service.LearningRoadmapService;
import com.prahlad.aijobportal.aiservice.security.principal.AuthenticatedUser;
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
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/learning-roadmap")
@RequiredArgsConstructor
@Tag(name = "AI Learning Roadmap", description = "Beginner-to-advanced learning path generated for the authenticated candidate")
public class LearningRoadmapController {

    private final LearningRoadmapService learningRoadmapService;

    @GetMapping
    @Operation(summary = "Generate a learning roadmap for the authenticated candidate based on their current skills and the live job market")
    public ResponseEntity<ApiResponse<LearningRoadmapResponse>> getLearningRoadmap(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken) {
        LearningRoadmapResponse response = learningRoadmapService.generate(principal.userId(), bearerToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
