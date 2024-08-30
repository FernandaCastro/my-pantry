package com.fcastro.accountservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cache-config")
@ConfigurationPropertiesScan
public class CustomCacheProperties {

    //All in minutes
    private int roleTtl;
    private int membersTtl;
    private int aclTtl;
}
