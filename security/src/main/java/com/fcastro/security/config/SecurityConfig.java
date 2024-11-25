package com.fcastro.security.config;


import com.fcastro.security.authorization.CustomAuthorizationManager;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.handler.CustomAccessDeniedHandler;
import com.fcastro.security.core.handler.CustomAuthenticationEntryPointHandler;
import com.fcastro.security.core.jwt.JWTRequestFilter;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false) //Disable default implementation of MethodSecurity
@ConditionalOnProperty(prefix = "spring", value = "security.enabled", matchIfMissing = true, havingValue = "true")
public class SecurityConfig {

    private final JWTRequestFilter jwtRequestFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final SecurityPropertiesConfig propertiesConfig;

    public SecurityConfig(JWTRequestFilter jwtRequestFilter,
                          CustomAccessDeniedHandler customAccessDeniedHandler,
                          CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler,
                          SecurityPropertiesConfig propertiesConfig) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPointHandler = customAuthenticationEntryPointHandler;
        this.propertiesConfig = propertiesConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.STORAGE));

        http
                .cors((cors) -> cors
                        .configurationSource(corsConfigurationSource()))

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
                        .requestMatchers("/actuator/health").permitAll()
                        //.requestMatchers("/accountGroups/*/members").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(propertiesConfig.getAllowedOrigin()));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, "language"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
        //Injects CustomAuthorizationManager into preAuthorize Method Interceptor
    Advisor preAuthorize(CustomAuthorizationManager manager) {
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(manager);
    }

}
