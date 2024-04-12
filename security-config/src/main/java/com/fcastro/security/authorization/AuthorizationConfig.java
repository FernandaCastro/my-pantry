package com.fcastro.security.authorization;

import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AuthorizationConfig {

    private final String ROLE_SYSADMIN = "ROLE_SYSADMIN";

    //RestClient to the AuthorizationServer
    @Bean
    RestClient authorizationServer(SecurityPropertiesConfig securityConfigData, JWTHandler jwtHandler) {
        String jwtToken = jwtHandler.createSysToken("sysadmin@mypantry.com", ROLE_SYSADMIN);

        return RestClient.builder()
                .baseUrl(securityConfigData.getAuthzServer())
                .defaultHeader("SYSADMIN-AUTH", jwtToken)
                .build();

    }
}
