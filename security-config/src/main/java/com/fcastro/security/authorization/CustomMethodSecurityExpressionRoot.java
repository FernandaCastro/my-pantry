package com.fcastro.security.authorization;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

import java.util.List;

public class CustomMethodSecurityExpressionRoot {

    private MethodSecurityExpressionOperations expressionOperations;
    private final AuthorizationClient authorizationHandler;

    public CustomMethodSecurityExpressionRoot(AuthorizationClient authorizationHandler) {
        this.authorizationHandler = authorizationHandler;
    }

    public void setExpressionOperations(MethodSecurityExpressionOperations expressionOperations) {
        this.expressionOperations = expressionOperations;
    }

    /**
     * Check if connected user has <permission> in at least one group
     **/
    public boolean hasPermissionInAnyGroup(String permission) {

        String email = expressionOperations.getAuthentication().getName();

        return authorizationHandler.hasPermissionInAnyGroup(email, permission);
    }

    /** Check if connected user has <permission> in the <accountGroupId> **/
    public boolean hasPermissionInAGroup(Long accountGroupId, String permission) {

        if (accountGroupId == null || accountGroupId == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        return authorizationHandler.hasPermissionInAGroup(email, permission, accountGroupId);
    }

    /** Check if connected user has <permission> in the object informed **/
    public boolean hasPermissionInObject(String clazz, Long clazzId, String permission) {

        if (clazz == null || clazz.length() == 0 || clazzId == null || clazzId == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        return authorizationHandler.hasPermissionInObject(email, permission, clazz, clazzId);
    }

    /** Check if connected user has the <permission> in all the objects informed **/
    public boolean hasPermissionInObjectList(String clazz, List<Long> clazzIds, String permission) {

        if (clazz == null || clazz.length() == 0 || clazzIds == null || clazzIds.size() == 0) return false;

        String email = expressionOperations.getAuthentication().getName();

        return authorizationHandler.hasPermissionInObjectList(email, permission, clazz, clazzIds);
    }
}
