package com.prahlad.aijobportal.jobservice.category.service.impl;

import com.prahlad.aijobportal.jobservice.category.dto.response.JobCategoryResponse;
import com.prahlad.aijobportal.jobservice.category.mapper.JobCategoryMapper;
import com.prahlad.aijobportal.jobservice.category.repository.JobCategoryRepository;
import com.prahlad.aijobportal.jobservice.category.service.JobCategoryService;
import com.prahlad.aijobportal.jobservice.config.RedisCacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryRepository jobCategoryRepository;
    private final JobCategoryMapper jobCategoryMapper;

    @Override
    @Cacheable(RedisCacheConfig.JOB_CATEGORIES_CACHE)
    @Transactional(readOnly = true)
    public List<JobCategoryResponse> getAll() {
        return jobCategoryRepository.findAll().stream()
                .map(jobCategoryMapper::toResponse)
                .toList();
    }
}
