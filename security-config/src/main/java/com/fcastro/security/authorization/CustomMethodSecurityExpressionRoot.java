package com.fcastro.security.authorization;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

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

        var groupMember = authorizationHandler.hasPermissionInAnyGroup(email, permission);

        return groupMember != null && groupMember.size() > 0 ? true : false;
    }

    //Check if connected user has <permission> in the <groupId>
    public boolean hasPermissionInAGroup(Long accountGroupId, String permission) {

        if (accountGroupId == null || accountGroupId == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        var groupMember = authorizationHandler.hasPermissionInAGroup(email, permission, accountGroupId);
        return groupMember != null && groupMember.size() > 0 ? true : false;
    }

    //Check if connected user has <permission> in the group in which clazz/clazzId belongs
    public boolean hasPermissionInObject(String clazz, Long clazzId, String permission) {

        if (clazz == null || clazz.length() == 0 || clazzId == null || clazzId == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        var groupMember = authorizationHandler.hasPermissionInObject(email, permission, clazz, clazzId);
        return groupMember != null && groupMember.size() > 0 ? true : false;
    }
}
