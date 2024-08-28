package com.fcastro.security.authorization;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

import java.util.List;

public class CustomMethodSecurityExpressionRoot {

    private MethodSecurityExpressionOperations expressionOperations;
    private final AuthorizationHandler authorizationHandler;

    public CustomMethodSecurityExpressionRoot(AuthorizationHandler authorizationHandler) {
        this.authorizationHandler = authorizationHandler;
    }

    public void setExpressionOperations(MethodSecurityExpressionOperations expressionOperations) {
        this.expressionOperations = expressionOperations;
    }

    //Check if connected user has <role> : sysadmin user is the only one with a defined role
//    public boolean hasAuthority(String role) {
//        if (expressionOperations.getAuthentication().getAuthorities() == null) return false;
//        return expressionOperations.getAuthentication().getAuthorities().stream()
//                .anyMatch(grantedAuthority -> role.equalsIgnoreCase(grantedAuthority.getAuthority()));
//    }

    //Check if connected user has <permission> in at least one group
    public boolean hasPermissionInAnyGroup(String permission) {

        String email = expressionOperations.getAuthentication().getName();

        return authorizationHandler.hasPermissionInAnyGroup(email, permission);
    }

    //Check if connected user has <permission> in the <groupId>
    public boolean hasPermissionInAGroup(Long accountGroupId, String permission) {

        if (accountGroupId == null || accountGroupId == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        var groupMember = authorizationHandler.hasPermissionInAGroup(email, permission, accountGroupId);
        return groupMember != null && groupMember.size() > 0;
    }

    //Check if connected user has <permission> in the object informed
    public boolean hasPermissionInObject(String clazz, Long clazzId, String permission) {

        if (clazz == null || clazz.length() == 0 || clazzId == null || clazzId == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        var accessControl = authorizationHandler.hasPermissionInObject(email, permission, clazz, clazzId);
        return accessControl != null;

    }

    //Check if connected user has the <permission> in all informed objects
    public boolean hasPermissionInObjectList(String clazz, List<Long> clazzIds, String permission) {

        if (clazz == null || clazz.length() == 0 || clazzIds == null || clazzIds.size() == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        var list = authorizationHandler.hasPermissionInObjectList(email, permission, clazz, clazzIds);

        return list != null && list.size() == clazzIds.size();
    }
}
