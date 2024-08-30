package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.cache.MemberCacheDto;
import com.fcastro.accountservice.cache.MemberCacheService;
import com.fcastro.accountservice.config.CustomCacheConfig;
import com.fcastro.accountservice.role.Role;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {MemberCacheService.class})
@InitializeRedisContainer
@Import({RedisTestConfig.class, CustomCacheConfig.class})
public class GroupMemberCacheServiceIT {

    @MockBean
    private AccountGroupMemberRepository memberRepository;

    @Autowired
    private MemberCacheService memberCacheService;

    @SpyBean(name = CustomCacheConfig.MEMBER_CACHE)
    private Cache memberCache;

    private String anKey = "pantry@pantry.com";

    @BeforeEach
    void setUp() {

        //Reset Mock definitions
        reset(memberRepository);

        //Clear cache
        memberCache.clear();
    }

    @Test
    void givenEmptyCache_whenGetFromCache2Times_thenMissCacheThenHitCache() {
        //given
        var databaseList = List.of(
                AccountGroupMember.builder()
                        .accountGroupId(1L)
                        .accountId(1L)
                        .accountGroup(AccountGroup.builder().id(1L).build())
                        .role(Role.builder().id("OWNER").build())
                        .build()
        );
        var cacheList = List.of(
                MemberCacheDto.builder().accountGroupId(1L).roleId("OWNER").build()
        );
        when(memberRepository.findAllByEmail(anKey)).thenReturn(databaseList);

        //when
        List<MemberCacheDto> cacheMiss = memberCacheService.getFromCache(anKey);
        List<MemberCacheDto> cacheHit = memberCacheService.getFromCache(anKey);

        //then
        assertThat(cacheMiss).isEqualTo(cacheList);
        assertThat(cacheHit).isEqualTo(cacheList);

        verify(memberRepository, times(1)).findAllByEmail(anKey);
    }

    @Test
    void givenCacheWithOneKey_whenUpdateCache_thenAddToValues() {
        //given
        var anAccountGroup = AccountGroup.builder().id(2L).build();
        var anRole = "ADMIN";

        //Load given data to the Cache
        var givenMember = MemberCacheDto.builder().accountGroupId(1L).roleId("OWNER").build();
        List<MemberCacheDto> givenCacheValues = new ArrayList<>();
        givenCacheValues.add(givenMember);
        memberCache.put(anKey, givenCacheValues);

        //when
        memberCacheService.updateCache(anKey, anAccountGroup, anRole);

        //then
        List<MemberCacheDto> updateCache = new ArrayList();
        updateCache.add(givenMember);
        updateCache.add(MemberCacheDto.builder()
                .accountGroupId(anAccountGroup.getId())
                .roleId(anRole)
                .build());
        verify(memberCache, times(1)).put(anKey, updateCache);

        var finalCache = (List<MemberCacheDto>) memberCache.get(anKey).get();
        assertThat(finalCache).isEqualTo(updateCache);
    }

    @Test
    void givenEmptyCache_whenUpdateCache_thenDoNothing() {
        //given
        var anAccountGroup = AccountGroup.builder().id(2L).build();
        var anRole = "ADMIN";

        var newMember = MemberCacheDto.builder()
                .accountGroupId(anAccountGroup.getId())
                .roleId(anRole)
                .build();

        var updatedCacheValuees = new ArrayList<MemberCacheDto>();
        updatedCacheValuees.add(newMember);

        //when
        memberCacheService.updateCache(anKey, anAccountGroup, anRole);

        //then
        verify(memberCache, times(0)).put(anKey, updatedCacheValuees);
    }

    @Test
    void givenCacheWithOneKey_whenDeleteFromCache_thenRemoveOneItemFromValues() {
        //given
        //Load given data to the Cache
        var givenMember1 = MemberCacheDto.builder().accountGroupId(1L).roleId("OWNER").build();
        var givenMember2 = MemberCacheDto.builder().accountGroupId(2L).roleId("ADMIN").build();

        List<MemberCacheDto> givenCacheValues = new ArrayList<>();
        givenCacheValues.add(givenMember1);
        givenCacheValues.add(givenMember2);
        memberCache.put(anKey, givenCacheValues);

        //when
        memberCacheService.deleteFromCache(anKey, 2L, "ADMIN");

        //then
        List<MemberCacheDto> updateCacheValues = new ArrayList();
        updateCacheValues.add(givenMember1);
        verify(memberCache, times(1)).put(anKey, updateCacheValues);

        var finalCache = (List<MemberCacheDto>) memberCache.get(anKey).get();
        assertThat(finalCache).isEqualTo(updateCacheValues);
    }

    @Test
    void givenCacheWithOneKey_whenDeleteFromCache_thenRemoveEntireKeyFromCache() {
        //given
        //Load given data to the Cache
        var givenMember1 = MemberCacheDto.builder().accountGroupId(1L).roleId("OWNER").build();

        List<MemberCacheDto> givenCacheValues = new ArrayList<>();
        givenCacheValues.add(givenMember1);
        memberCache.put(anKey, givenCacheValues);

        //when
        memberCacheService.deleteFromCache(anKey, 1L, "OWNER");

        //then
        verify(memberCache, times(1)).evict(anKey);

        var finalCache = memberCache.get(anKey);
        assertThat(finalCache).isNull();
    }

    @Test
    void givenCacheWithTwoKeys_whenDeleteAllFromCache_thenRemoveAllKeysOrValuesReferringAGroup() {
        //given
        var anKey2 = "pantry2@gmail.com";
        var group2 = 2L;

        //Load data into cache
        var givenAndKept = MemberCacheDto.builder().accountGroupId(1L).roleId("OWNER").build(); //Expected to be the only entry left in cache
        var givenAndDeleted1 = MemberCacheDto.builder().accountGroupId(group2).roleId("ADMIN").build(); //Expected to be removed from cache
        var givenAndDeleted2 = MemberCacheDto.builder().accountGroupId(group2).roleId("OWNER").build(); //Expected to be removed from cache

        List<MemberCacheDto> givenValuesKey1 = new ArrayList<>();
        givenValuesKey1.add(givenAndKept);
        givenValuesKey1.add(givenAndDeleted1);
        memberCache.put(anKey, givenValuesKey1);

        List<MemberCacheDto> givenValuesKey2 = new ArrayList<>();
        givenValuesKey2.add(givenAndDeleted2);
        memberCache.put(anKey2, givenValuesKey2);
        //

        var databaseList = List.of(
                AccountGroupMember.builder()
                        .accountGroupId(2L)
                        .accountGroup(AccountGroup.builder().id(2L).build())
                        .accountId(1L)
                        .account(Account.builder().id(1L).email(anKey).build())
                        .role(Role.builder().id("ADMIN").build())
                        .build(),
                AccountGroupMember.builder()
                        .accountGroupId(2L)
                        .accountGroup(AccountGroup.builder().id(2L).build())
                        .accountId(2L)
                        .account(Account.builder().id(2L).email(anKey2).build())
                        .role(Role.builder().id("OWNER").build())
                        .build()
        );
        when(memberRepository.findAllByAccountGroupId(group2)).thenReturn(databaseList);


        //when
        memberCacheService.deleteAllFromCache(group2);

        //then
        List<MemberCacheDto> updateCache = new ArrayList();
        updateCache.add(givenAndKept);

        var finalCacheEmail1 = (List<MemberCacheDto>) memberCache.get(anKey).get();
        assertThat(finalCacheEmail1).isEqualTo(updateCache);  //key1 is kept, but its values lists just the givenAndKept

        var finalCacheEmail2 = memberCache.get(anKey2);  //key2 was completely deleted from cache
        assertThat(finalCacheEmail2).isNull();
    }
}
