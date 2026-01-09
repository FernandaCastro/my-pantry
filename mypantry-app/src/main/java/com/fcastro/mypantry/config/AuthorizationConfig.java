package com.fcastro.mypantry.config;


import com.fcastro.security.authorization.CustomAuthorizationManager;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.handler.CustomAccessDeniedHandler;
import com.fcastro.security.core.handler.CustomAuthenticationEntryPointHandler;
import com.fcastro.security.core.jwt.JWTHandler;
import com.fcastro.security.core.jwt.JWTRequestFilter;
import org.springframework.aop.Advisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.client.RestClient;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false) //Disable default implementation of MethodSecurity
public class AuthorizationConfig {

    private final JWTRequestFilter jwtRequestFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final SecurityPropertiesConfig propertiesConfig;
    private final CorsConfig corsConfig;

    private final String ROLE_SYSADMIN = "ROLE_SYSADMIN";

    public AuthorizationConfig(JWTRequestFilter jwtRequestFilter,
                               CustomAccessDeniedHandler customAccessDeniedHandler,
                               CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler,
                               SecurityPropertiesConfig propertiesConfig, CorsConfig corsConfig) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPointHandler = customAuthenticationEntryPointHandler;
        this.propertiesConfig = propertiesConfig;
        this.corsConfig = corsConfig;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.STORAGE));

        http
                .cors((cors) -> cors
                        .configurationSource(corsConfig.corsConfigurationSource()))

                //As it's an SPA stateless there's no need to protect against CSRF
                //TODO: Need to confirm this!!!
                .csrf((csrf) -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

                //Intercept the FilterChain(ExceptionTranslationFilter) when a SecurityException occurs:
                // return 401(Unauthorized) when AuthenticationEntryPoint is called
                // return 403(Forbidden) when AccessDeniedHandler is called
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPointHandler))

                .authorizeHttpRequests(authorize -> authorize
                        //Dispatches FORWARD and ERROR are permitted to allow Spring MVC to render views and Spring Boot to render errors
                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        //.requestMatchers("/accountGroups/*/members").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }


    @Bean
        //Injects CustomAuthorizationManager into preAuthorize Method Interceptor
    Advisor preAuthorize(CustomAuthorizationManager manager) {
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(manager);
    }

}
