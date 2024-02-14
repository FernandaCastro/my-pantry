package com.fcastro.account;

import com.fcastro.app.model.AccountDto;
import com.fcastro.security.config.JWTHandler;
import com.fcastro.security.config.SecurityConfigData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {SecurityConfigData.class})
@ComponentScan(basePackages = {"com.fcastro.security"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JWTHandlerTest {

    @Autowired
    SecurityConfigData securityConfigData;

    @InjectMocks
    JWTHandler jwtHandler;

//    @BeforeAll
//    void setup() {
//        securityConfigData = new SecurityConfigData();
//        jwtHandler = new JWTHandler(securityConfigData);
//    }

    @Configuration
    @ComponentScan(basePackages = {"com.fcastro.security"}) //Otherwise, it doesn't load SecurityConfig classes
    static class TestConfig {

    }

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
        assertEquals(accountDto.getId().toString(), authentication.getPrincipal());

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
