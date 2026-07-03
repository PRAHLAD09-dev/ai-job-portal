package com.prahlad.aijobportal.aiservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Redis caches, per DAY07_AI_SERVICE.md's "Redis Cache" section: Resume
 * Analysis, Recommendations, Popular Skills, Interview Questions. AI
 * responses are expensive (external API cost + latency) so these get a
 * longer TTL than the transactional caches in other services.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String RESUME_ANALYSIS_CACHE = "resumeAnalysis";
    public static final String JOB_RECOMMENDATIONS_CACHE = "jobRecommendations";
    public static final String CANDIDATE_RECOMMENDATIONS_CACHE = "candidateRecommendations";
    public static final String POPULAR_SKILLS_CACHE = "popularSkills";
    public static final String INTERVIEW_QUESTIONS_CACHE = "interviewQuestions";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                RESUME_ANALYSIS_CACHE, defaultConfig.entryTtl(Duration.ofHours(6)),
                JOB_RECOMMENDATIONS_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)),
                CANDIDATE_RECOMMENDATIONS_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)),
                POPULAR_SKILLS_CACHE, defaultConfig.entryTtl(Duration.ofHours(12)),
                INTERVIEW_QUESTIONS_CACHE, defaultConfig.entryTtl(Duration.ofHours(6))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
