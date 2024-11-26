package com.fcastro.security.core.web;

import com.fcastro.security.core.config.SecurityPropertiesConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SecurityPropertiesConfig propertiesConfig;

    public WebConfig(SecurityPropertiesConfig securityConfigData) {
        this.propertiesConfig = securityConfigData;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(propertiesConfig.getAllowedOrigin())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

