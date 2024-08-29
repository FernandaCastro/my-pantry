package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.accountservice.cache.AccessControlCacheService;
import com.fcastro.accountservice.cache.MemberCacheService;
import com.fcastro.accountservice.role.RoleService;
import org.springframework.stereotype.Service;

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

    //Authorization methods
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

    //Check if connected user has <permission> in at least one group
//    public boolean hasPermissionInAnyGroup(String email, String permission) {
//
//        var groupMembers = accountGroupMemberService.getAllByEmail(email);
//        if (groupMembers == null || groupMembers.size() == 0) return false;
//
//        for (AccountGroupMemberDto gm : groupMembers) {
//            if (checkPermission(gm.getRole().getPermissions(), permission)) return true;
//        }
//
//        return false;
//    }
//
//    //Check if connected user has <permission> in the <groupId>
//    public boolean hasPermission(String email, Long groupId, String permission) {
//
//        if (groupId == null || groupId == 0) return false;
//        return checkAccountGroupPermission(email, groupId, permission);
//    }
//
//    //Check if connected user has <permission> in the group in which clazz/clazzId belongs
//    public boolean hasPermission(String email, String clazz, Long clazzId, String permission) {
//
//        if (clazz == null || clazz.length() == 0 || clazzId == null || clazzId == 0) return false;
//        var access = accessControlService.get(clazz, clazzId);
//
//        return checkAccountGroupPermission(email, access.getAccountGroup().getId(), permission);
//    }
//
//    private boolean checkAccountGroupPermission(String email, Long groupId, String permission) {
//        var groupMember = accountGroupMemberService.createChildGroupMember(email, groupId);
//        if (groupMember == null || groupMember.getRole() == null) return false;
//
//        return checkPermission(groupMember.getRole().getPermissions(), permission);
//    }
//
//    private boolean checkPermission(List<PermissionDto> permissions, String permission) {
//        if (permissions == null || permissions.size() == 0) return false;
//
//        return permissions.stream()
//                .anyMatch((p) -> p.getId().equalsIgnoreCase(permission));
//    }

}
