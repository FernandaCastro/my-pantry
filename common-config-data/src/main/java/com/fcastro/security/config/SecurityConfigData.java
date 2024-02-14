package com.fcastro.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security-config")
@ConfigurationPropertiesScan
public class SecurityConfigData {

    private String secret;
    private String allowedOrigin;
    private String googleClientId;
    private String googleClientSecret;
}
