package com.prahlad.aijobportal.jobservice.category.service;

import com.prahlad.aijobportal.jobservice.category.dto.response.JobCategoryResponse;

import java.util.List;

public interface JobCategoryService {

    List<JobCategoryResponse> getAll();
}
