package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.config.CacheConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupMemberCacheService {

    private final AccountGroupMemberRepository repository;
    private final Cache memberCache;

    public GroupMemberCacheService(@Qualifier(CacheConfig.MEMBER_CACHE) Cache memberCache, AccountGroupMemberRepository repository) {
        this.repository = repository;
        this.memberCache = memberCache;
    }

    @Cacheable(value = CacheConfig.MEMBER_CACHE, key = "#email")
    @Transactional
    public List<GroupMemberCacheDto> getFromCache(String email) {

        var list = repository.findAllByEmail(email).stream()
                .map(i -> GroupMemberCacheDto.builder()
                        .accountGroupId(i.getAccountGroupId())
                        .parentAccountGroupId(i.getAccountGroup().getParentAccountGroup() == null ? null : i.getAccountGroup().getParentAccountGroup().getId())
                        .roleId(i.getRole().getId())
                        .build())
                .collect(Collectors.toList());

        return list;
    }

    // Method to update cache only if key exists
    public void updateCache(String email, AccountGroup group, String roleId) {

        // Check if the key exists
        Cache.ValueWrapper valueWrapper = memberCache.get(email);
        if (valueWrapper == null) return;

        // Key exists, proceed to update it
        List<GroupMemberCacheDto> list = (List<GroupMemberCacheDto>) valueWrapper.get();
        list.add(GroupMemberCacheDto.builder()
                .accountGroupId(group.getId())
                .parentAccountGroupId(group.getParentAccountGroup() != null ? group.getParentAccountGroup().getId() : null)
                .roleId(roleId)
                .build());
        memberCache.put(email, list);

    }

    // Method to remove item from values  or delete the key completely.
    public void deleteFromCache(String email, Long groupId, String roleId) {

        // Check if the key exists
        Cache.ValueWrapper valueWrapper = memberCache.get(email);
        if (valueWrapper == null) return;

        // Key exists, proceed to update it
        List<GroupMemberCacheDto> list = (List<GroupMemberCacheDto>) valueWrapper.get();
        list.remove(GroupMemberCacheDto.builder()
                .accountGroupId(groupId)
                .roleId(roleId)
                .build());

        if (list.size() > 0) {
            memberCache.put(email, list); //update the values of the key
        } else {
            memberCache.evict(email); //Remove the entire key
        }
    }

    //Method to remover all references to the accountGroupId that may exist in cache
    public void deleteAllFromCache(Long accountGroupId) {

        //Get all members of the group
        var list = repository.findAllByAccountGroupId(accountGroupId);
        if (list.size() == 0) return;

        list.forEach(member -> {
            deleteFromCache(member.getAccount().getEmail(),
                    member.getAccountGroupId(),
                    member.getRole().getId());
        });
    }
}
