package com.prahlad.aijobportal.aiservice.skillgap.service;

import com.prahlad.aijobportal.aiservice.skillgap.dto.response.SkillGapResponse;

import java.util.UUID;

public interface SkillGapService {

    /** Compares the candidate's current skills against currently open jobs to surface missing skills and career suggestions. */
    SkillGapResponse analyze(UUID candidateId, String bearerToken);
}
