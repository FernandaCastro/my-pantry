package com.fcastro.accountservice;

import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.account.AccountRepository;
import com.fcastro.accountservice.account.AccountService;
import com.fcastro.accountservice.accountgroup.AccountGroupService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTHandler;
import com.fcastro.security.core.model.AccountDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
//@ComponentScan(basePackages = {"com.fcastro.accountService", "com.fcastro.service.core"})
//Loads the application.yml file
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = SecurityPropertiesConfig.class)
public class AccountServiceUnitTest {

    @Autowired
    SecurityPropertiesConfig propertiesConfig;


    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountGroupMemberService accountGroupMemberService;

    @Mock
    AccountGroupService accountGroupService;

    @Mock
    GoogleIdTokenVerifier googleVerifier;

    @Mock
    JWTHandler jwtHandler;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    public void whenLogin_createJwtToken() {
        //given
        AccountDto account = AccountDto.builder().id(1L).email("user@user.com").build();
        when(accountRepository.findByEmail(anyString())).thenReturn(
                Optional.of(Account.builder().id(account.getId()).email(account.getEmail()).build()));
        when(jwtHandler.createToken(anyString(), anyBoolean())).thenReturn("jwtToken");
        //when
        var appToken = accountService.login("user@user.com");

        //then
        assertThat(appToken).isNotNull();
        assertThat(appToken.getToken()).isNotBlank();
        assertThat(appToken.getAccount()).isNotNull();
        assertThat(appToken.getAccount().getEmail()).isEqualTo("user@user.com");
    }
}
