package com.prahlad.aijobportal.candidateservice.skill.service.impl;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateLookupService;
import com.prahlad.aijobportal.candidateservice.candidate.service.ProfileCompletionService;
import com.prahlad.aijobportal.candidateservice.skill.dto.request.SkillRequest;
import com.prahlad.aijobportal.candidateservice.skill.dto.response.SkillResponse;
import com.prahlad.aijobportal.candidateservice.skill.entity.Skill;
import com.prahlad.aijobportal.candidateservice.skill.exception.SkillAlreadyExistsException;
import com.prahlad.aijobportal.candidateservice.skill.exception.SkillNotFoundException;
import com.prahlad.aijobportal.candidateservice.skill.mapper.SkillMapper;
import com.prahlad.aijobportal.candidateservice.skill.repository.SkillRepository;
import com.prahlad.aijobportal.candidateservice.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final CandidateLookupService candidateLookupService;
    private final ProfileCompletionService profileCompletionService;

    @Override
    @Transactional
    public SkillResponse create(UUID userId, SkillRequest request) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        if (skillRepository.existsByCandidateIdAndNameIgnoreCase(candidate.getId(), request.name())) {
            throw new SkillAlreadyExistsException("You have already added the skill '" + request.name() + "'");
        }

        Skill skill = skillMapper.toEntity(request);
        skill.setCandidate(candidate);

        Skill saved = skillRepository.save(skill);
        profileCompletionService.recalculate(candidate);

        log.info("Created skill id={} for candidateId={}", saved.getId(), candidate.getId());
        return skillMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAll(UUID userId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);
        return skillRepository.findByCandidateId(candidate.getId()).stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SkillResponse update(UUID userId, UUID skillId, SkillRequest request) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Skill skill = skillRepository.findByIdAndCandidateId(skillId, candidate.getId())
                .orElseThrow(() -> new SkillNotFoundException(skillId));

        if (!skill.getName().equalsIgnoreCase(request.name())
                && skillRepository.existsByCandidateIdAndNameIgnoreCase(candidate.getId(), request.name())) {
            throw new SkillAlreadyExistsException("You have already added the skill '" + request.name() + "'");
        }

        skillMapper.updateEntityFromRequest(request, skill);
        Skill saved = skillRepository.save(skill);

        log.info("Updated skill id={} for candidateId={}", saved.getId(), candidate.getId());
        return skillMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID skillId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Skill skill = skillRepository.findByIdAndCandidateId(skillId, candidate.getId())
                .orElseThrow(() -> new SkillNotFoundException(skillId));

        skillRepository.delete(skill);
        profileCompletionService.recalculate(candidate);

        log.info("Deleted skill id={} for candidateId={}", skillId, candidate.getId());
    }
}
