package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.accountservice.cache.AccessControlCacheService;
import com.fcastro.accountservice.cache.MemberCacheDto;
import com.fcastro.accountservice.cache.MemberCacheService;
import com.fcastro.accountservice.role.RoleService;
import com.fcastro.security.core.model.PermissionDto;
import com.fcastro.security.core.model.RoleDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceUnitTest {

    @Mock
    AccessControlService accessControlService;

    @Mock
    AccountGroupMemberService accountGroupMemberService;

    @Mock
    RoleService roleService;

    @Mock
    MemberCacheService groupMemberCacheService;

    @Mock
    AccessControlCacheService accessControlCacheService;

    @InjectMocks
    AuthorizationService authorizationService;

    @Test
    public void givenPemissionFound_whenHasPermissionInAGroup_returnTrue() {
        //given
        String email = "pantry@pantry.com";
        String permission = "list_pantry";
        long accountGroupId = 1L;

        //Member of a different group found in cache
        List<MemberCacheDto> givenMemberCache = new ArrayList<>();
        givenMemberCache.add(MemberCacheDto.builder()
                .accountGroupId(2L)
                .roleId("USER")
                .build());
        when(groupMemberCacheService.getFromCache(email)).thenReturn(givenMemberCache);

        //A Role found in cache
        RoleDto givenRole = RoleDto.builder()
                .id("USER")
                .permissions(List.of(PermissionDto.builder().id("list_pantry").build()))
                .build();
        when(roleService.getRole("USER")).thenReturn(givenRole);

        //when
        var hasPermission = authorizationService.hasPermissionInAnyGroup(email, permission);

        //then
        assertThat(hasPermission).isTrue();
    }

    @Test
    public void givenPemissionNotFound_whenHasPermissionInAGroup_returnFalse() {
        //given
        String email = "pantry@pantry.com";
        String permission = "create_pantry";
        long accountGroupId = 1L;

        //Member of a different group found in cache
        List<MemberCacheDto> givenMemberCache = new ArrayList<>();
        givenMemberCache.add(MemberCacheDto.builder()
                .accountGroupId(2L)
                .roleId("USER")
                .build());
        when(groupMemberCacheService.getFromCache(email)).thenReturn(givenMemberCache);

        //A Role found in cache
        RoleDto givenRole = RoleDto.builder()
                .id("USER")
                .permissions(List.of(PermissionDto.builder().id("list_pantry").build()))
                .build();
        when(roleService.getRole("USER")).thenReturn(givenRole);

        //when
        var hasPermission = authorizationService.hasPermissionInAnyGroup(email, permission);

        //then
        assertThat(hasPermission).isFalse();
    }

    @Test
    public void givenPemissionFound_whenHasPermissionInGroup_returnTrue() {
        //given
        String email = "pantry@pantry.com";
        String permission = "create_pantry";
        long accountGroupId = 1L;

        //Member of the group found in cache
        List<MemberCacheDto> givenMemberCache = new ArrayList<>();
        givenMemberCache.add(MemberCacheDto.builder()
                .accountGroupId(1L)
                .roleId("OWNER")
                .build());
        when(groupMemberCacheService.getFromCache(email)).thenReturn(givenMemberCache);

        //A Role found in cache
        RoleDto givenRole = RoleDto.builder()
                .id("OWNER")
                .permissions(List.of(PermissionDto.builder().id("create_pantry").build()))
                .build();
        when(roleService.getRole("OWNER")).thenReturn(givenRole);

        //when
        var hasPermission = authorizationService.hasPermissionInGroup(email, permission, accountGroupId);

        //then
        assertThat(hasPermission).isTrue();
    }

    @Test
    public void givenPemissionNotFound_whenHasPermissionInGroup_returnFalse() {
        //given
        String email = "pantry@pantry.com";
        String permission = "create_pantry";
        long accountGroupId = 1L;

        //Member of the group found in cache
        List<MemberCacheDto> givenMemberCache = new ArrayList<>();
        givenMemberCache.add(MemberCacheDto.builder()
                .accountGroupId(1L)
                .roleId("USER")
                .build());
        when(groupMemberCacheService.getFromCache(email)).thenReturn(givenMemberCache);

        //A Role found in cache, but it does not list the requested permission
        RoleDto givenRole = RoleDto.builder()
                .id("USER")
                .permissions(List.of(PermissionDto.builder().id("list_pantry").build()))
                .build();
        when(roleService.getRole("USER")).thenReturn(givenRole);

        //when
        var hasPermission = authorizationService.hasPermissionInGroup(email, permission, accountGroupId);

        //then
        assertThat(hasPermission).isFalse();
    }

    @Test
    public void givenPemissionFound_whenHasPermissionInObject_returnTrue() {
        //given
        String email = "pantry@pantry.com";
        String permission = "create_pantry";
        String clazz = "Pantry";
        long clazzId = 10L;
        long ObjectAccountGroupId = 1L;

        //Member of the group found in cache
        List<MemberCacheDto> givenMemberCache = new ArrayList<>();
        givenMemberCache.add(MemberCacheDto.builder()
                .accountGroupId(ObjectAccountGroupId)
                .roleId("OWNER")
                .build());
        when(groupMemberCacheService.getFromCache(email)).thenReturn(givenMemberCache);

        //Object (clazz + clazzId) found in AccessControl cache
        Set<Long> givenAccessControlCache = new HashSet<>();
        givenAccessControlCache.add(2L);
        givenAccessControlCache.add(10L);
        when(accessControlCacheService.getFromCache(ObjectAccountGroupId, clazz)).thenReturn(givenAccessControlCache);

        //A Role found in cache
        RoleDto givenRole = RoleDto.builder()
                .id("OWNER")
                .permissions(List.of(PermissionDto.builder().id("create_pantry").build()))
                .build();
        when(roleService.getRole("OWNER")).thenReturn(givenRole);

        //when
        var hasPermission = authorizationService.hasPermissionInObject(email, permission, clazz, clazzId);

        //then
        assertThat(hasPermission).isTrue();
    }

    @Test
    public void givenPemissionNotFound_whenHasPermissionInObject_returnFalse() {
        //given
        String email = "pantry@pantry.com";
        String permission = "create_pantry";
        String clazz = "Pantry";
        long clazzId = 10L;
        long ObjectAccountGroupId = 1L;

        //Member of the group found in cache
        List<MemberCacheDto> givenMemberCache = new ArrayList<>();
        givenMemberCache.add(MemberCacheDto.builder()
                .accountGroupId(ObjectAccountGroupId)
                .roleId("OWNER")
                .build());
        when(groupMemberCacheService.getFromCache(email)).thenReturn(givenMemberCache);

        //Object (clazz + clazzId) NOT found in AccessControl cache
        Set<Long> givenAccessControlCache = new HashSet<>();
        givenAccessControlCache.add(2L);
        givenAccessControlCache.add(20L);
        when(accessControlCacheService.getFromCache(ObjectAccountGroupId, clazz)).thenReturn(givenAccessControlCache);

        //when
        var hasPermission = authorizationService.hasPermissionInObject(email, permission, clazz, clazzId);

        //then
        assertThat(hasPermission).isFalse();
    }
}
