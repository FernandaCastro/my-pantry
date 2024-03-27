package com.fcastro.security.authorization;

import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AuthorizationConfig {

    private final String ROLE_SYSADMIN = "ROLE_SYSADMIN";

    @Bean
        //RestClient to the AuthorizationServer
    RestClient authorizationServer(SecurityPropertiesConfig securityConfigData, JWTHandler jwtHandler) {
        String jwtToken = jwtHandler.createToken("sysadmin@mypantry.com", ROLE_SYSADMIN, false);

        return RestClient.builder()
                .baseUrl(securityConfigData.getAuthzServer())
                .defaultHeader("SYSADMIN-AUTH", jwtToken)
                .build();

    }
}
