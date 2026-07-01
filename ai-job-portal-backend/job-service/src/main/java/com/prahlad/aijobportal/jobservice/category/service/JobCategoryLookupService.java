package com.prahlad.aijobportal.jobservice.category.service;

import com.prahlad.aijobportal.jobservice.category.entity.JobCategory;
import com.prahlad.aijobportal.jobservice.category.repository.JobCategoryRepository;
import com.prahlad.aijobportal.jobservice.job.exception.JobCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobCategoryLookupService {

    private final JobCategoryRepository jobCategoryRepository;

    public JobCategory getByIdOrThrow(UUID categoryId) {
        return jobCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new JobCategoryNotFoundException(categoryId));
    }
}
