package com.fcastro.security.config;


//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = false) //Disable default implementation of MethodSecurity
//@ConditionalOnProperty(prefix = "spring", value = "security.enabled", matchIfMissing = true, havingValue = "true")
public class SecurityConfig {

//    private final JWTRequestFilter jwtRequestFilter;
//    private final CustomAccessDeniedHandler customAccessDeniedHandler;
//    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
//    private final SecurityPropertiesConfig propertiesConfig;
//
//    public SecurityConfig(JWTRequestFilter jwtRequestFilter,
//                          CustomAccessDeniedHandler customAccessDeniedHandler,
//                          CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler,
//                          SecurityPropertiesConfig propertiesConfig) {
//        this.jwtRequestFilter = jwtRequestFilter;
//        this.customAccessDeniedHandler = customAccessDeniedHandler;
//        this.customAuthenticationEntryPointHandler = customAuthenticationEntryPointHandler;
//        this.propertiesConfig = propertiesConfig;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.STORAGE));
//
//        http
//                .cors((cors) -> cors
//                        .configurationSource(corsConfigurationSource()))
//
//                //As it's an SPA stateless there's no need to protect against CSRF
//                //TODO: Need to confirm this!!!
//                .csrf((csrf) -> csrf.disable())
//
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
//
//                //Intercept the FilterChain(ExceptionTranslationFilter) when a SecurityException occurs:
//                // return 401(Unauthorized) when AuthenticationEntryPoint is called
//                // return 403(Forbidden) when AccessDeniedHandler is called
//                .exceptionHandling(exception -> exception
//                        .accessDeniedHandler(customAccessDeniedHandler)
//                        .authenticationEntryPoint(customAuthenticationEntryPointHandler))
//
//                .authorizeHttpRequests(authorize -> authorize
//                        //Dispatches FORWARD and ERROR are permitted to allow Spring MVC to render views and Spring Boot to render errors
//                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
//                        .requestMatchers("/actuator/health").permitAll()
//                        //.requestMatchers("/accountGroups/*/members").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }
//
//
//    @Bean
//        //Injects CustomAuthorizationManager into preAuthorize Method Interceptor
//    Advisor preAuthorize(CustomAuthorizationManager manager) {
//        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(manager);
//    }

}
