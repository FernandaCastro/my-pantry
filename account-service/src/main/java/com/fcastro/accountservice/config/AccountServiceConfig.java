package com.fcastro.accountservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class AccountServiceConfig {

    @Bean
    GoogleIdTokenVerifier googleIdVerifier(SecurityPropertiesConfig propertiesConfig) {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(propertiesConfig.getGoogleClientId()))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }
}
