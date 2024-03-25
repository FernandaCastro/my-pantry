package com.fcastro.accountservice.config;

import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTHandler;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.Collections;

@Configuration
public class AccountServiceConfig {

    private final String ROLE_SYSADMIN = "ROLE_SYSADMIN";

    @Bean
    GoogleIdTokenVerifier googleIdVerifier(SecurityPropertiesConfig propertiesConfig) {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(propertiesConfig.getGoogleClientId()))
                .build();
    }

    @Bean
    RestClient accessControlDomain(JWTHandler jwtHandler) {
        String jwtToken = jwtHandler.createToken("sysadmin@mypantry.com", ROLE_SYSADMIN, false);

        return RestClient.builder()
                .defaultHeader("SYSADMIN-AUTH", jwtToken)
                .build();

    }
}
