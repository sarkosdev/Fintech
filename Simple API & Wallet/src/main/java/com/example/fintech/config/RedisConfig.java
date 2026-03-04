package com.example.fintech.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * Redis configuration class
 *
 * 1. Enable Spring Cache abstraction
 * 2. Configure Redis CacheManager
 * 3. Configure RedisTemplate in order to interact directly with Redis
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * CacheManager Bean Configuration  Class
     *
     * Springs CacheManager integrates Redis with Springs caching abstraction
     * Allows the use of the following annotations:
     *
     * @Cacheable
     * @Cacheput
     * @CacheEvict
     *
     * Configuration applied here defines that cached entries live for 1 day
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))  // Cache expires after 1 day
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    /**
     * RedisTemplate bean configuration
     *
     * RedisTemplate is the main abstraction used to interact directly with Redis
     *
     * Keys stored as String
     * Values are serialized as JSON
     *
     *
     * @param redisConnectionFactory
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        //template.getConnectionFactory().getConnection().flushDb();

        // Configure the RedisTemplate to use a custom serializer
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setDefaultSerializer(serializer);
        template.setValueSerializer(serializer);
        template.setKeySerializer(RedisSerializer.string());
        template.afterPropertiesSet();
        template.getConnectionFactory().getConnection().flushDb();      // Reset DB everytime application restarts

        return template;
    }

}
