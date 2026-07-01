package com.prahlad.aijobportal.jobservice.category.controller;

import com.prahlad.aijobportal.jobservice.category.dto.response.JobCategoryResponse;
import com.prahlad.aijobportal.jobservice.category.service.JobCategoryService;
import com.prahlad.aijobportal.jobservice.skill.service.PopularSkillsService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public reference-data endpoints: job categories (seeded via Flyway)
 * and the most popular skill names across all listed jobs. Both are
 * whitelisted as public GET in {@code SecurityConfig}.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Job Categories", description = "Job category and popular skill reference data")
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;
    private final PopularSkillsService popularSkillsService;

    @GetMapping(CommonConstants.API_BASE_PATH + "/job-categories")
    @Operation(summary = "Get all job categories")
    public ResponseEntity<ApiResponse<List<JobCategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(jobCategoryService.getAll()));
    }

    @GetMapping(CommonConstants.API_BASE_PATH + "/job-categories/popular-skills")
    @Operation(summary = "Get the most popular skill names across all listed jobs")
    public ResponseEntity<ApiResponse<List<String>>> getPopularSkills() {
        return ResponseEntity.ok(ApiResponse.success(popularSkillsService.getPopularSkills()));
    }
}
