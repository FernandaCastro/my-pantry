package com.fcastro.pantryservice;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextFactory {

    public static void createSecurityContext(String username) {
        SecurityContextHolder.setContext(
                SecurityContextHolder.createEmptyContext()
        );
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(username, null)
        );
    }
}
