package com.fcastro.security.core.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTHandler jwtUtils;

    public JWTRequestFilter(JWTHandler jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication;

        //pantry-web users
        Cookie[] cookies = request.getCookies();
        Cookie authCookie = cookies == null ? null : Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("AUTH-TOKEN"))
                .findAny().orElse(null);
        if (authCookie != null && (authentication = jwtUtils.verifyAndGetAuthentication(authCookie.getValue())) != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //Internal requests
        String jwtToken = request.getHeader("SYSADMIN-AUTH");
        if (jwtToken != null && (authentication = jwtUtils.verifyAndGetAuthentication(jwtToken)) != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}