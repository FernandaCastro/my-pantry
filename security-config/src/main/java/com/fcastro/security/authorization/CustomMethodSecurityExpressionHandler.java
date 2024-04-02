package com.fcastro.security.authorization;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final AuthorizationHandler authorizationService;

    CustomMethodSecurityExpressionHandler(AuthorizationHandler authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        StandardEvaluationContext context = (StandardEvaluationContext) super.createEvaluationContext(authentication, mi);
        MethodSecurityExpressionOperations delegate = (MethodSecurityExpressionOperations) context.getRootObject().getValue();
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authorizationService);
        root.setExpressionOperations(delegate);
        context.setRootObject(root);
        return context;
    }
}
