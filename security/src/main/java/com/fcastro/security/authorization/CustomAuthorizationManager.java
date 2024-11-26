package com.fcastro.security.authorization;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class CustomAuthorizationManager implements AuthorizationManager<MethodInvocation> {

    private final CustomMethodSecurityExpressionHandler customMethodSecurityExpressionHandler;

    public CustomAuthorizationManager(CustomMethodSecurityExpressionHandler customMethodSecurityExpressionHandler) {
        this.customMethodSecurityExpressionHandler = customMethodSecurityExpressionHandler;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation invocation) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(invocation.getMethod().getAnnotation(PreAuthorize.class).value());

        EvaluationContext evaluationContext = customMethodSecurityExpressionHandler.createEvaluationContext(authentication, invocation);
        boolean granted = Boolean.TRUE.equals(expression.getValue(evaluationContext, Boolean.class));

        return new AuthorizationDecision(granted);
    }
}
