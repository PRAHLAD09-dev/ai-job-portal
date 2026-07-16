package com.prahlad.aijobportal.recruiterservice.feign;

import com.prahlad.aijobportal.recruiterservice.feign.dto.ResumeAnalysisResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Synchronous Feign client to AI Service's internal-only "AI Match"
 * endpoint (never routed through the API Gateway), backing the DAY11
 * Recruiter Dashboard. Authenticates via
 * {@link InternalFeignClientConfig}'s internal-service-token
 * interceptor, since a recruiter's own bearer token has no standing to
 * ask AI Service about an arbitrary candidate's resume analysis.
 * Resolved via Eureka using the registered service id ({@code AI-SERVICE}).
 */
@FeignClient(name = "AI-SERVICE", path = CommonConstants.API_BASE_PATH + "/ai/internal/recruiter", configuration = InternalFeignClientConfig.class)
public interface AiServiceClient {

    @GetMapping("/resume-analysis/{candidateId}")
    ApiResponse<ResumeAnalysisResponse> getLatestResumeAnalysis(@PathVariable("candidateId") UUID candidateId);
}
