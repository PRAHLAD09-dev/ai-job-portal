package com.prahlad.aijobportal.jobservice.config;

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
 * Configures Redis-backed caching for read-heavy, slow-changing job
 * listing data (latest jobs, featured jobs, categories, popular
 * skills) per DAY05_JOB_SERVICE.md. Caches are invalidated explicitly
 * via {@code @CacheEvict} whenever a job is created, updated, published,
 * or closed — never relying on TTL alone for correctness — with a
 * conservative TTL as a safety net.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String LATEST_JOBS_CACHE = "latestJobs";
    public static final String FEATURED_JOBS_CACHE = "featuredJobs";
    public static final String TRENDING_JOBS_CACHE = "trendingJobs";
    public static final String JOB_CATEGORIES_CACHE = "jobCategories";
    public static final String POPULAR_SKILLS_CACHE = "popularSkills";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                LATEST_JOBS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(5)),
                FEATURED_JOBS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)),
                TRENDING_JOBS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)),
                JOB_CATEGORIES_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)),
                POPULAR_SKILLS_CACHE, defaultConfig.entryTtl(Duration.ofHours(1))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
