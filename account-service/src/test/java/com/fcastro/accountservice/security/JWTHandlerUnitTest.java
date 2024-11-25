package com.fcastro.accountservice.security;

import com.fcastro.accountservice.account.AccountDto;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
//Loads the application.aaa file
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class,
        classes = JWTHandler.class)
@EnableConfigurationProperties(value = SecurityPropertiesConfig.class)
@ComponentScan(basePackages = {"com.fcastro.security.core.jwt"})
public class JWTHandlerUnitTest {

    @Autowired
    SecurityPropertiesConfig propertiesConfig;

    @Autowired
    JWTHandler jwtHandler;

    @Test
    void givenWebUser_createToken_and_verifyAndGetAuthentication_shouldWork() {
        //given
        AccountDto accountDto = createMockAccountDto();
        String role = null;
        boolean rememberMe = false;

        //when
        String token = jwtHandler.createToken(accountDto.getEmail(), rememberMe);
        Authentication authentication = jwtHandler.verifyAndGetAuthentication(token);

        //then
        assertNotNull(token);
        assertNotNull(authentication);
        assertEquals(accountDto.getEmail(), authentication.getPrincipal());
        assertEquals(0, authentication.getAuthorities().size());
    }

    @Test
    void givenSysadmin_createToken_and_verifyAndGetAuthentication_shouldWork() {
        //given
        AccountDto accountDto = createMockAccountDto();
        String role = "ROLE_SYSADMIN";
        boolean rememberMe = false;

        //when
        String token = jwtHandler.createSysToken(accountDto.getEmail(), "ROLE_SYSADMIN");
        Authentication authentication = jwtHandler.verifyAndGetAuthentication(token);

        //then
        assertNotNull(token);
        assertNotNull(authentication);
        assertEquals(accountDto.getEmail(), authentication.getPrincipal());
        assertEquals(1, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SYSADMIN")));

    }

    private AccountDto createMockAccountDto() {
        return AccountDto.builder()
                .id(1L)
                .name("user")
                .email("user@email.com")
                .build();
    }
}
