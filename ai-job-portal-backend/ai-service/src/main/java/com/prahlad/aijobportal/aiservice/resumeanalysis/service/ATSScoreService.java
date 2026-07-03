package com.prahlad.aijobportal.aiservice.resumeanalysis.service;

import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request.AnalyzeResumeRequest;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ATSScoreResponse;

public interface ATSScoreService {

    ATSScoreResponse score(AnalyzeResumeRequest request);
}
