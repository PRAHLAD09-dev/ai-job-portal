package com.prahlad.aijobportal.aiservice.jobdescription.service;

import com.prahlad.aijobportal.aiservice.jobdescription.dto.request.JobDescriptionRequest;
import com.prahlad.aijobportal.aiservice.jobdescription.dto.response.JobDescriptionResponse;

public interface JobDescriptionService {

    JobDescriptionResponse generate(JobDescriptionRequest request);
}
