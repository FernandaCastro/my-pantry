package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.accountservice.cache.AccessControlCacheService;
import com.fcastro.accountservice.cache.MemberCacheService;
import com.fcastro.accountservice.role.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AuthorizationService {

    private final AccessControlService accessControlService;
    private final AccountGroupMemberService accountGroupMemberService;
    private final RoleService roleService;
    private final MemberCacheService memberCacheService;
    private final AccessControlCacheService accessControlCacheService;

    public AuthorizationService(AccessControlService accessControlService, AccountGroupMemberService accountGroupMemberService, RoleService roleService, MemberCacheService memberCacheService, AccessControlCacheService accessControlCacheService) {
        this.accessControlService = accessControlService;
        this.accountGroupMemberService = accountGroupMemberService;
        this.roleService = roleService;
        this.memberCacheService = memberCacheService;
        this.accessControlCacheService = accessControlCacheService;
    }

    public boolean hasPermissionInAnyGroup(String email, String permission) {

        //Get Groups/Roles from MemberCache, otherwise from database
        var groupMemberList = memberCacheService.getFromCache(email);

        //Get Roles from RoleCache, otherwise from database
        var permissionFound = groupMemberList.stream()
                .map(member -> roleService.getRole(member.getRoleId()))
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getId().equalsIgnoreCase(permission));

        return permissionFound;
    }

    public boolean hasPermissionInGroup(String email, String permission, Long accountGroupId) {

        //Get Groups/Roles from MemberCache, otherwise from database
        var memberList = memberCacheService.getFromCache(email);

        //Get Roles from RoleCache, otherwise from database
        var permissionFound = memberList.stream()
                .filter(i -> i.getAccountGroupId() == accountGroupId)
                .map(member -> roleService.getRole(member.getRoleId()))
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getId().equalsIgnoreCase(permission));

        return permissionFound;
    }

    public boolean hasPermissionInObject(String email, String permission, String clazz, Long clazzId) {

        //Get Groups/Roles from MemberCache, otherwise from database
        var memberList = memberCacheService.getFromCache(email);

        //Find the correct AccountGroupId and verify the permission in it
        var permissionFound = memberList.stream()

                //Find the AccountGroupId the Object belongs to
                .flatMap(member -> accessControlCacheService.getFromCache(member.getAccountGroupId(), clazz).stream()
                        .filter(id -> id.equals(clazzId))  // Filter to match the clazzId
                        .map(i -> member) // Map to the MemberCacheDto
                )
                //Verify the permission according to member.roleId
                .map(member -> roleService.getRole(member.getRoleId()))
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getId().equalsIgnoreCase(permission));

        return permissionFound;
    }

    /**
     * Verify if user has access to ALL clazzIds and the permission in all the groups
     **/
    public boolean hasPermissionInObjectList(String email, String permission, String clazz, List<Long> clazzIds) {
        //Get Groups/Roles from MemberCache, otherwise from database
        var memberList = memberCacheService.getFromCache(email);

        AtomicInteger count = new AtomicInteger();
        //Find the correct AccountGroupId and verify the permission in it
        var permissionFound = memberList.stream()

                //Find all the AccountGroupIds the Objects in classIds list belong to
                .flatMap(member -> accessControlCacheService.getFromCache(member.getAccountGroupId(), clazz).stream()
                        .filter(clazzIds::contains)  // Filter to match the clazzId
                        .map(i -> {
                            count.getAndIncrement();
                            return member;
                        }) // Map to the MemberCacheDto
                )
                //Verify the permission in all accountGroups found, according to member.roleId in each
                .map(member -> roleService.getRole(member.getRoleId()))
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getId().equalsIgnoreCase(permission));

        var hasAccessToObjects = count.get() == clazzIds.size();
        return permissionFound && hasAccessToObjects;
    }

}
