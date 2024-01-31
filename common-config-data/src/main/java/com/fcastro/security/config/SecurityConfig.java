package com.fcastro.security.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTRequestFilter jwtRequestFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

    @Value("${app.allowed-origin}")
    private String allowedOrigin;

    public SecurityConfig(JWTRequestFilter jwtRequestFilter, CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPointHandler = customAuthenticationEntryPointHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.STORAGE));

        http
                .cors((cors) -> cors
                        .configurationSource(corsConfigurationSource()))

                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringRequestMatchers("/oauth/login")
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

                //Intercept the FilterChain(ExceptionTranslationFilter) when a SecurityException occurs:
                // return 401(Unauthorized) when AuthenticationEntryPoint is called
                // return 403(Forbidden) when AccessDeniedHandler is called
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPointHandler))

                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/oauth/logout"))
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("AUTH-TOKEN")
                        .addLogoutHandler(clearSiteData)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .permitAll())

                .authorizeHttpRequests(authorize -> authorize
                        //Dispatches FORWARD and ERROR are permitted to allow Spring MVC to render views and Spring Boot to render errors
                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers("/oauth/login", "/oauth/logout").permitAll()
                        .requestMatchers("/account/group").hasRole("ADMIN")
                        .anyRequest().hasRole("USER"));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigin));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, HttpHeaders.ORIGIN, "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
    private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
         * the CsrfToken when it is rendered in the response body.
         */
        this.delegate.handle(request, response, csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler
         * to resolve the CsrfToken. This applies when a single-page application includes
         * the header value automatically, which was obtained via a cookie containing the
         * raw CsrfToken.
         */
        if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
            return super.resolveCsrfTokenValue(request, csrfToken);
        }
        /*
         * In all other cases (e.g. if the request contains a request parameter), use
         * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
         * when a server-side rendered form includes the _csrf request parameter as a
         * hidden input.
         */
        return this.delegate.resolveCsrfTokenValue(request, csrfToken);
    }
}

final class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        // Render the token value to a cookie by causing the deferred token to be loaded
        csrfToken.getToken();

        filterChain.doFilter(request, response);
    }
}
