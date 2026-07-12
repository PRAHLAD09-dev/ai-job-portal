package com.prahlad.aijobportal.applicationservice.config;

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
 * Configures Redis-backed caching for the Application Service, per
 * DAY06_APPLICATION_SERVICE.md's "Redis Cache" section (Recent
 * Applications, Recruiter Dashboard, Candidate Dashboard, Application
 * Statistics). Caches are invalidated explicitly via {@code @CacheEvict}
 * on every status change, with a conservative TTL as a safety net.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String RECENT_APPLICATIONS_CACHE = "recentApplications";
    public static final String RECRUITER_DASHBOARD_CACHE = "recruiterDashboard";
    public static final String CANDIDATE_DASHBOARD_CACHE = "candidateDashboard";
    public static final String APPLICATION_STATISTICS_CACHE = "applicationStatistics";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.EVERYTHING);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                RECENT_APPLICATIONS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)),
                RECRUITER_DASHBOARD_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)),
                CANDIDATE_DASHBOARD_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)),
                APPLICATION_STATISTICS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
