package com.prahlad.aijobportal.aiservice.learningroadmap.service;

import com.prahlad.aijobportal.aiservice.learningroadmap.dto.response.LearningRoadmapResponse;

import java.util.UUID;

public interface LearningRoadmapService {

    /**
     * Builds a beginner-to-advanced learning path for the candidate,
     * targeting the skills currently missing for roles they'd want
     * based on the live job market sample.
     */
    LearningRoadmapResponse generate(UUID candidateId, String bearerToken);
}
