package com.prahlad.aijobportal.jobservice.skill.service;

import com.prahlad.aijobportal.jobservice.config.RedisCacheConfig;
import com.prahlad.aijobportal.jobservice.skill.repository.JobSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Returns the most frequently listed skill names across all jobs, per
 * DAY05's "Popular Skills" caching requirement.
 */
@Service
@RequiredArgsConstructor
public class PopularSkillsService {

    private static final int TOP_N = 20;

    private final JobSkillRepository jobSkillRepository;

    @Cacheable(RedisCacheConfig.POPULAR_SKILLS_CACHE)
    @Transactional(readOnly = true)
    public List<String> getPopularSkills() {
        return jobSkillRepository.findPopularSkillNames(PageRequest.of(0, TOP_N));
    }
}
