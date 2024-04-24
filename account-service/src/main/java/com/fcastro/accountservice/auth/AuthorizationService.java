package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.security.core.model.AccountGroupMemberDto;
import com.fcastro.security.core.model.PermissionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationService {

    private final AccessControlService accessControlService;
    private final AccountGroupMemberService accountGroupMemberService;

    public AuthorizationService(AccessControlService accessControlService, AccountGroupMemberService accountGroupMemberService) {
        this.accessControlService = accessControlService;
        this.accountGroupMemberService = accountGroupMemberService;
    }


    //Check if connected user has <permission> in at least one group
    public boolean hasPermissionInAnyGroup(String email, String permission) {

        var groupMembers = accountGroupMemberService.getAllByEmail(email);
        if (groupMembers == null || groupMembers.size() == 0) return false;

        for (AccountGroupMemberDto gm : groupMembers) {
            if (checkPermission(gm.getRole().getPermissions(), permission)) return true;
        }

        return false;
    }

    //Check if connected user has <permission> in the <groupId>
    public boolean hasPermission(String email, Long groupId, String permission) {

        if (groupId == null || groupId == 0) return false;
        return checkAccountGroupPermission(email, groupId, permission);
    }

    //Check if connected user has <permission> in the group in which clazz/clazzId belongs
    public boolean hasPermission(String email, String clazz, Long clazzId, String permission) {

        if (clazz == null || clazz.length() == 0 || clazzId == null || clazzId == 0) return false;
        var access = accessControlService.get(clazz, clazzId);

        return checkAccountGroupPermission(email, access.getAccountGroup().getId(), permission);
    }

    private boolean checkAccountGroupPermission(String email, Long groupId, String permission) {
        var groupMember = accountGroupMemberService.createChildGroupMember(email, groupId);
        if (groupMember == null || groupMember.getRole() == null) return false;

        return checkPermission(groupMember.getRole().getPermissions(), permission);
    }

    private boolean checkPermission(List<PermissionDto> permissions, String permission) {
        if (permissions == null || permissions.size() == 0) return false;

        return permissions.stream()
                .anyMatch((p) -> p.getName().equalsIgnoreCase(permission));
    }

}
