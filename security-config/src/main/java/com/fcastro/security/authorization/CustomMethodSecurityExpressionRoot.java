package com.fcastro.security.authorization;

import com.fcastro.security.accesscontrol.AccessControlService;
import com.fcastro.security.core.model.AccountGroupMemberDto;
import com.fcastro.security.core.model.PermissionDto;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

import java.util.List;

public class CustomMethodSecurityExpressionRoot {

    private MethodSecurityExpressionOperations expressionOperations;
    private final AuthorizationHandler authorizationService;
    private final AccessControlService accessControlService;

    public CustomMethodSecurityExpressionRoot(AuthorizationHandler authorizationService, AccessControlService accessControlService) {
        this.authorizationService = authorizationService;
        this.accessControlService = accessControlService;
    }

    public void setExpressionOperations(MethodSecurityExpressionOperations expressionOperations) {
        this.expressionOperations = expressionOperations;
    }

    //Check if connected user has <role> : sysadmin user is the only one with a defined role
    public boolean hasAuthority(String role) {
        if (expressionOperations.getAuthentication().getAuthorities() == null) return false;
        return expressionOperations.getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> role.equalsIgnoreCase(grantedAuthority.getAuthority()));
    }

    //Check if connected user has <permission> in at least one group
    public boolean hasPermission(String permission) {

        String email = expressionOperations.getAuthentication().getName();
        var groupMembers = authorizationService.getGroupMember(email);
        if (groupMembers == null || groupMembers.size() == 0) return false;

        for (AccountGroupMemberDto gm : groupMembers) {
            if (checkPermission(gm.getRole().getPermissions(), permission)) return true;
        }

        return false;
    }

    //Check if connected user has <permission> in the <groupId>
    public boolean hasPermission(Long groupId, String permission) {

        if (groupId == null) return hasPermission(permission);

        String email = expressionOperations.getAuthentication().getName();
        var groupMember = authorizationService.getGroupMember(groupId, email);
        if (groupMember == null || groupMember.getRole() == null) return false;

        return checkPermission(groupMember.getRole().getPermissions(), permission);

    }

    //Check if connected user has <permission> in the <groupId>
    public boolean hasPermission(String clazz, Long clazzId, String permission) {

        if (clazz == null || clazz.length() == 0 || clazzId == null || clazzId == 0) return false;
        var access = accessControlService.get(clazz, clazzId);

        String email = expressionOperations.getAuthentication().getName();
        var groupMember = authorizationService.getGroupMember(access.getAccountGroupId(), email);
        if (groupMember == null || groupMember.getRole() == null) return false;

        return checkPermission(groupMember.getRole().getPermissions(), permission);

    }

    private boolean checkPermission(List<PermissionDto> permissions, String permission) {
        if (permissions == null || permissions.size() == 0) return false;

        return permissions.stream()
                .anyMatch((p) -> p.getName().equalsIgnoreCase(permission));
    }

}
