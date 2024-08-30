package com.fcastro.utils;

import com.fcastro.accountservice.config.CustomCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@EnableCaching
@TestConfiguration
@EnableConfigurationProperties(value = CustomCacheProperties.class)
public class RedisTestConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        var redisConnectionFactory = new LettuceConnectionFactory();
        return redisConnectionFactory;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .build();
    }
}
