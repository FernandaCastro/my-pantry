package com.fcastro.accountservice.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CustomCacheConfig {

    public static final String MEMBER_CACHE = "members";
    public static final String ROLE_CACHE = "rolePermissions";
    public static final String ACCESS_CONTROL_CACHE = "acl";

    private final CustomCacheProperties cacheProperties;

    public CustomCacheConfig(CustomCacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(3))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration(MEMBER_CACHE,
                        cacheConfiguration().entryTtl(Duration.ofMinutes(cacheProperties.getMembersTtl())))
                .withCacheConfiguration(ROLE_CACHE,
                        cacheConfiguration().entryTtl(Duration.ofMinutes(cacheProperties.getRoleTtl())))
                .withCacheConfiguration(ACCESS_CONTROL_CACHE,
                        cacheConfiguration().entryTtl(Duration.ofMinutes(cacheProperties.getAclTtl())));
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

}
