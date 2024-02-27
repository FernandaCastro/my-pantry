package com.fcastro.account;

import com.fcastro.security.jwt.JWTHandler;
import com.fcastro.security.jwt.SecurityConfigData;
import com.fcastro.security.model.AccountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
//Loads the application.yml file
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class,
        classes = JWTHandler.class)
@EnableConfigurationProperties(value = SecurityConfigData.class)
public class JWTHandlerUnitTest {

    @Autowired
    SecurityConfigData securityConfigData;

    @Autowired
    JWTHandler jwtHandler;

    @Test
    void createToken_and_verifyAndGetAuthentication_shouldWork() {
        // Arrange
        AccountDto accountDto = createMockAccountDto();
        boolean rememberMe = false;

        // Act
        String token = jwtHandler.createToken(accountDto, rememberMe);
        Authentication authentication = jwtHandler.verifyAndGetAuthentication(token);

        // Assert
        assertNotNull(token);
        assertNotNull(authentication);
        assertEquals(accountDto.getEmail(), authentication.getPrincipal());

        var authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(","));

        assertEquals(accountDto.getRoles(), authorities);
    }

    private AccountDto createMockAccountDto() {
        return AccountDto.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .roles("ROLE_USER")
                .build();
    }
}
