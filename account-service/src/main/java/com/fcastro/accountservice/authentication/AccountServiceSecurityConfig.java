package com.fcastro.accountservice.authentication;


//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@ConditionalOnProperty(prefix = "spring", value = "security.enabled", matchIfMissing = true, havingValue = "true")
public class AccountServiceSecurityConfig {

//    private final JWTRequestFilter jwtRequestFilter;
//    private final CustomAccessDeniedHandler customAccessDeniedHandler;
//    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
//    private final SecurityPropertiesConfig propertiesConfig;
//    private final UserDetailsServiceImpl userDetailsService;
//
//    public AccountServiceSecurityConfig(JWTRequestFilter jwtRequestFilter,
//                                        CustomAccessDeniedHandler customAccessDeniedHandler,
//                                        CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler,
//                                        SecurityPropertiesConfig propertiesConfig,
//                                        UserDetailsServiceImpl userDetailsService) {
//        this.jwtRequestFilter = jwtRequestFilter;
//        this.customAccessDeniedHandler = customAccessDeniedHandler;
//        this.customAuthenticationEntryPointHandler = customAuthenticationEntryPointHandler;
//        this.propertiesConfig = propertiesConfig;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Bean
//    public SecurityFilterChain authenticationFilterChain(HttpSecurity http) throws Exception {
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
//
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
//
//                //Intercept the FilterChain(ExceptionTranslationFilter) when a SecurityException occurs:
//                // return 401(Unauthorized) when AuthenticationEntryPoint is called
//                // return 403(Forbidden) when AccessDeniedHandler is called
//                .exceptionHandling(exception -> exception
//                        .accessDeniedHandler(customAccessDeniedHandler)
//                        .authenticationEntryPoint(customAuthenticationEntryPointHandler))
//
//                .logout((logout) -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/accountservice/auth/logout"))
//                        .clearAuthentication(true)
//                        .invalidateHttpSession(true)
//                        .deleteCookies("AUTH-TOKEN")
//                        .addLogoutHandler(clearSiteData)
//                        .logoutSuccessHandler((request, response, authentication) -> {
//                            response.setStatus(HttpServletResponse.SC_OK);
//                        })
//                        .permitAll())
//
//                .authorizeHttpRequests(authorize -> authorize
//                        //Dispatches FORWARD and ERROR are permitted to allow Spring MVC to render views and Spring Boot to render errors
//                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
//                        .requestMatchers("/accountservice/auth/**").permitAll()
//                        .requestMatchers("/actuator/**").permitAll()
//                        .anyRequest().authenticated());
//
//        return http.build();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}

