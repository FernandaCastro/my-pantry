package com.fcastro.accountservice.cache;

import com.fcastro.accountservice.accesscontrol.AccessControl;
import com.fcastro.accountservice.accesscontrol.AccessControlRepository;
import com.fcastro.accountservice.config.CustomCacheConfig;
import com.fcastro.accountservice.exception.InvalidCacheKeyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AccessControlCacheService {

    private final Pattern keyPattern = Pattern.compile("(?<groupId>\\d):(?<clazz>[a-zA-Z]+)");
    private final AccessControlRepository accessControlRepository;
    private final Cache accessControlCache;

    public AccessControlCacheService(@Qualifier(CustomCacheConfig.ACCESS_CONTROL_CACHE) Cache accessControlCache, AccessControlRepository accessControlRepository) {
        this.accessControlRepository = accessControlRepository;
        this.accessControlCache = accessControlCache;
    }

    public String encodeKey(Long accountGroupId, String clazz) {
        if (accountGroupId == 0 || clazz == null || clazz.isEmpty() || clazz.isBlank())
            throw new InvalidCacheKeyException("accountGroupId: " + accountGroupId + ", clazz: " + clazz);
        return accountGroupId + ":" + clazz;
    }

    private long decodeAccountGroupIdKey(String key) {
        if (key == null || key.isEmpty() || key.isBlank())
            throw new InvalidCacheKeyException("key: " + key);

        var matcher = keyPattern.matcher(key);
        if (!matcher.matches()) throw new InvalidCacheKeyException("key: " + key);

        return Long.parseLong(matcher.group("groupId"));
    }

    private String decodeClazzKey(String key) {
        if (key == null || key.isEmpty() || key.isBlank())
            throw new InvalidCacheKeyException("key: " + key);

        var matcher = keyPattern.matcher(key);
        if (!matcher.matches()) throw new InvalidCacheKeyException("key: " + key);

        return matcher.group("clazz");
    }

    @Cacheable(value = CustomCacheConfig.ACCESS_CONTROL_CACHE, key = "#root.target.encodeKey(#accountGroupId,#clazz)")
    @Transactional
    public Set<Long> getFromCache(long accountGroupId, String clazz) {

        var clazzIds = accessControlRepository.findAllByAccountGroupIdAndClazz(accountGroupId, clazz).stream()
                .map(AccessControl::getClazzId)
                .collect(Collectors.toSet());

        return clazzIds;
    }

    // Method to update cache only if key exists
    public void updateCache(long accountGroupId, String clazz, long clazzId) {

        var key = encodeKey(accountGroupId, clazz);

        // Check if the key exists
        Cache.ValueWrapper valueWrapper = accessControlCache.get(key);
        if (valueWrapper == null) return;

        // Key exists, proceed to update it
        Set<Long> list = (Set<Long>) valueWrapper.get();
        list.add(clazzId);
        accessControlCache.put(key, list);
    }

    // Method to remove item from values  or delete the key completely.
    public void deleteFromCache(long accountGroupId, String clazz, long clazzId) {

        var key = encodeKey(accountGroupId, clazz);

        // Check if the key exists
        Cache.ValueWrapper valueWrapper = accessControlCache.get(key);
        if (valueWrapper == null) return;

        // Key exists, proceed to update it
        Set<Long> list = (Set<Long>) valueWrapper.get();
        list.remove(clazzId);

        if (list.size() > 0) {
            accessControlCache.put(key, list); //update the values of the key
        } else {
            accessControlCache.evict(key); //Remove the entire key
        }
    }
}
