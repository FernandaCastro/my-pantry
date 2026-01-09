package com.fcastro.mypantry.config;

import com.fcastro.security.core.config.SecurityPropertiesConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CorsConfig corsConfig;

    public WebConfig(SecurityPropertiesConfig securityConfigData, CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsConfig.getAllowedOrigin())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

