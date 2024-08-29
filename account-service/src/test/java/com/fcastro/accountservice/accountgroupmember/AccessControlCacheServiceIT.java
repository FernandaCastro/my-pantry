package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.accountservice.accesscontrol.AccessControl;
import com.fcastro.accountservice.accesscontrol.AccessControlRepository;
import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.cache.AccessControlCacheService;
import com.fcastro.accountservice.cache.MemberCacheDto;
import com.fcastro.accountservice.config.CacheConfig;
import com.fcastro.utils.InitializeRedisContainer;
import com.fcastro.utils.RedisTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AccessControlCacheService.class})
@InitializeRedisContainer
@Import({RedisTestConfig.class, CacheConfig.class})

public class AccessControlCacheServiceIT {

    @MockBean
    private AccessControlRepository accessControlRepository;

    @Autowired
    private AccessControlCacheService accessControlCacheService;

    @SpyBean(name = CacheConfig.ACCESS_CONTROL_CACHE)
    private Cache accessControlCache;

    private String anKey = "pantry@pantry.com";

    @BeforeEach
    void setUp() {

        //Reset Mock definitions
        reset(accessControlRepository);

        //Clear cache
        accessControlCache.clear();
    }

    @Test
    void givenEmptyCache_whenGetFromCache2Times_thenMissCacheThenHitCache() {
        //given
        long accountGroupId = 1L;
        String clazz = "Pantry";
        long clazzId = 10L;

        var cacheList = Set.of(clazzId);

        var databaseList = List.of(
                AccessControl.builder()
                        .accountGroup(AccountGroup.builder().id(accountGroupId).build())
                        .clazz(clazz)
                        .clazzId(clazzId)
                        .build()
        );
        when(accessControlRepository.findAllByAccountGroupIdAndClazz(accountGroupId, clazz)).thenReturn(databaseList);

        //when
        Set<Long> cacheMiss = accessControlCacheService.getFromCache(accountGroupId, clazz);
        Set<Long> cacheHit = accessControlCacheService.getFromCache(accountGroupId, clazz);

        //then
        assertThat(cacheMiss).isEqualTo(cacheList);
        assertThat(cacheHit).isEqualTo(cacheList);

        verify(accessControlRepository, times(1)).findAllByAccountGroupIdAndClazz(accountGroupId, clazz);
    }

    @Test
    void givenCache_whenUpdateCache_thenAddToValues() {
        //given
        long accountGroupId = 1L;
        String clazz = "Pantry";
        long existingClazzId = 10L;
        long newClassId = 20L;
        String key = accountGroupId + ":" + clazz;

        //Populate cache with the given key/values
        var givenCache = new HashSet<Long>();
        givenCache.add(existingClazzId);
        accessControlCache.put(key, givenCache);

        //when
        accessControlCacheService.updateCache(accountGroupId, clazz, newClassId);

        //then
        Set<Long> updateCache = new HashSet<>();
        updateCache.add(existingClazzId);
        updateCache.add(newClassId);
        verify(accessControlCache, times(1)).put(key, updateCache);

        var finalCache = (Set<Long>) accessControlCache.get(key).get();
        assertThat(finalCache).isEqualTo(updateCache);
    }

    @Test
    void givenEmptyCache_whenUpdateCache_thenDoNothing() {
        //given
        long accountGroupId = 1L;
        String clazz = "Pantry";
        long clazzId = 10L;
        String key = accountGroupId + ":" + clazz;

        var givenCache = new HashSet<Long>();
        givenCache.add(clazzId);

        //when
        accessControlCacheService.updateCache(accountGroupId, clazz, clazzId);

        //then
        verify(accessControlCache, times(0)).put(anKey, givenCache);

    }

    @Test
    void givenCache_whenDeleteFromCache_thenRemoveOneItemFromValues() {
        //given
        long accountGroupId = 1L;
        String clazz = "Pantry";
        long existingClazzId = 10L;
        long toBeDeletedClassId = 20L;
        String key = accountGroupId + ":" + clazz;

        //Populate cache with the given key/values
        var givenCache = new HashSet<Long>();
        givenCache.add(existingClazzId);
        givenCache.add(toBeDeletedClassId);
        accessControlCache.put(key, givenCache);

        //when
        accessControlCacheService.deleteFromCache(accountGroupId, clazz, toBeDeletedClassId);

        //then
        Set<Long> updateCache = new HashSet<>();
        updateCache.add(existingClazzId);
        verify(accessControlCache, times(1)).put(key, updateCache);

        var finalCache = (Set<MemberCacheDto>) accessControlCache.get(key).get();
        assertThat(finalCache).isEqualTo(updateCache);
    }

    @Test
    void givenCache_whenDeleteFromCache_thenRemoveEntireKeyFromCache() {
        //given
        long accountGroupId = 1L;
        String clazz = "Pantry";
        long existingClazzId = 10L;
        String key = accountGroupId + ":" + clazz;

        //Populate cache with the given key/values
        var givenCache = new HashSet<Long>();
        givenCache.add(existingClazzId);
        accessControlCache.put(key, givenCache);

        //when
        accessControlCacheService.deleteFromCache(accountGroupId, clazz, existingClazzId);

        //then
        verify(accessControlCache, times(1)).evict(key);

        var finalCache = accessControlCache.get(anKey);
        assertThat(finalCache).isNull();
    }
}
