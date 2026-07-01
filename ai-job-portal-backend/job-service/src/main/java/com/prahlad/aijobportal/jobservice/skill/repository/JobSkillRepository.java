package com.prahlad.aijobportal.jobservice.skill.repository;

import com.prahlad.aijobportal.jobservice.skill.entity.JobSkill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JobSkillRepository extends JpaRepository<JobSkill, UUID> {

    List<JobSkill> findByJobId(UUID jobId);

    @Query("select s.name from JobSkill s group by s.name order by count(s.name) desc")
    List<String> findPopularSkillNames(Pageable pageable);
}
