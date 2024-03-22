package com.fcastro.security.authorization;

import com.fcastro.security.model.AccountGroupMemberDto;
import com.fcastro.security.model.PermissionDto;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

import java.util.List;

public class CustomMethodSecurityExpressionRoot {

    private final MethodSecurityExpressionOperations expressionOperations;
    private final AuthorizationService authorizationService;

    public CustomMethodSecurityExpressionRoot(MethodSecurityExpressionOperations operations, AuthorizationService authorizationService) {
        this.expressionOperations = operations;
        this.authorizationService = authorizationService;
    }

    //Check if connected user has <permission> in the <groupId>
    public boolean hasPermission(Long groupId, String permission) {

        if (groupId == null) return hasPermission(permission);

        String email = expressionOperations.getAuthentication().getName();
        var groupMember = authorizationService.getGroupMember(groupId, email);
        if (groupMember == null || groupMember.getRole() == null) return false;

        return checkPermission(groupMember.getRole().getPermissions(), permission);

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

    private boolean checkPermission(List<PermissionDto> permissions, String permission) {
        if (permissions == null || permissions.size() == 0) return false;

        return permissions.stream()
                .anyMatch((p) -> p.getName().toLowerCase().equals(permission.toLowerCase()));
    }

}
