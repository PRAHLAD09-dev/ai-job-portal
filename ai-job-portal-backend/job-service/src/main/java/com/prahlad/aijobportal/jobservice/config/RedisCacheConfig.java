package com.prahlad.aijobportal.jobservice.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
        // NOTE: the 2-arg activateDefaultTyping(validator, typing) overload defaults
        // to JsonTypeInfo.As.WRAPPER_ARRAY, which wraps EVERY non-final value
        // (including List/ArrayList itself) as a ["type", value] JSON array. For a
        // @Cacheable method returning List<JobCategoryResponse>, that produces a
        // nested wrapper-array structure. On read-back, RedisCache resolves the
        // target type from the cache value's raw type (generics erased at runtime),
        // so the deserializer ends up expecting a JSON object where it instead
        // finds the wrapper array -> "Unexpected token (START_ARRAY), expected
        // START_OBJECT". Spring's own zero-arg GenericJackson2JsonRedisSerializer
        // avoids this by using PROPERTY-based typing (an "@class" field inside each
        // object) instead of WRAPPER_ARRAY - use the explicit 3-arg overload to get
        // the same, collection-safe behavior on our custom ObjectMapper (needed here
        // for JavaTimeModule support).
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY);

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
