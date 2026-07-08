package com.prahlad.aijobportal.aiservice.admin.service.impl;

import com.prahlad.aijobportal.aiservice.admin.dto.response.AiUsageStatisticsResponse;
import com.prahlad.aijobportal.aiservice.admin.service.AdminAiUsageService;
import com.prahlad.aijobportal.aiservice.interview.repository.InterviewQuestionRepository;
import com.prahlad.aijobportal.aiservice.recommendation.repository.JobRecommendationRepository;
import com.prahlad.aijobportal.aiservice.resumeanalysis.repository.ResumeAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAiUsageServiceImpl implements AdminAiUsageService {

    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final JobRecommendationRepository jobRecommendationRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    @Override
    public AiUsageStatisticsResponse getUsageStatistics() {
        return new AiUsageStatisticsResponse(
                resumeAnalysisRepository.count(),
                jobRecommendationRepository.count(),
                interviewQuestionRepository.count()
        );
    }
}
