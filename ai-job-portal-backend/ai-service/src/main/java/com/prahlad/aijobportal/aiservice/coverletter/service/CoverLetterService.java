package com.prahlad.aijobportal.aiservice.coverletter.service;

import com.prahlad.aijobportal.aiservice.coverletter.dto.request.CoverLetterRequest;
import com.prahlad.aijobportal.aiservice.coverletter.dto.response.CoverLetterResponse;

public interface CoverLetterService {

    CoverLetterResponse generate(String bearerToken, CoverLetterRequest request);
}
