package com.fcastro.accountservice.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

    public static final String MEMBER_CACHE = "members";
    public static final String ROLE_CACHE = "rolePermissions";
    public static final String ACCESS_CONTROL_CACHE = "acl";


    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration())
//                .withCacheConfiguration(MEMBER_CACHE, RedisCacheConfiguration.defaultCacheConfig())
//                .withCacheConfiguration(ROLE_CACHE, RedisCacheConfiguration.defaultCacheConfig())
                .build();
    }

    /**
     * key: account.email,
     * values: List of MemberCacheDto
     **/
    @Bean
    public Cache members(CacheManager cacheManager) {
        return cacheManager.getCache(MEMBER_CACHE);
    }

    /**
     * key: role.id,
     * values: List of PermissionDto
     **/
    @Bean
    public Cache rolePermissions(CacheManager cacheManager) {
        return cacheManager.getCache(ROLE_CACHE);
    }

    /**
     * key: accountGroup.id:clazzType,
     * values: List of clazzId
     **/
    @Bean
    public Cache acl(CacheManager cacheManager) {
        return cacheManager.getCache(ACCESS_CONTROL_CACHE);
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        return template;
//    }

//    @Bean
//    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
//        return (builder) -> builder
//                .withCacheConfiguration("RoleCache",
//                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(2)))
//                .withCacheConfiguration("GroupMemberCache",
//                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(2)));
//    }
}