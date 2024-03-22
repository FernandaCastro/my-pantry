package com.fcastro.accountService;

import com.fcastro.accountService.account.Account;
import com.fcastro.accountService.account.AccountRepository;
import com.fcastro.accountService.account.AccountService;
import com.fcastro.accountService.accountGroup.AccountGroupService;
import com.fcastro.accountService.accountGroupMember.AccountGroupMemberService;
import com.fcastro.security.jwt.JWTHandler;
import com.fcastro.security.jwt.PropertiesConfig;
import com.fcastro.security.model.AccountDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = {"com.fcastro.accountService", "com.fcastro.service.jwt"})
//Loads the application.yml file
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = PropertiesConfig.class)
public class AccountServiceUnitTest {

    @Autowired
    PropertiesConfig propertiesConfig;


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

    @Autowired
    JWTHandler jwtHandler;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    public void whenLogin_createJwtToken() {
        //given
        AccountDto account = AccountDto.builder().id(1L).email("user@user.com.br").build();
        when(accountRepository.findByEmail(anyString())).thenReturn(
                Optional.of(Account.builder().id(account.getId()).email(account.getEmail()).build()));

        //when
        var appToken = accountService.login("user@user.com");

        //then
        assertThat(appToken).isNotNull();
        assertThat(appToken.getToken()).isNotBlank();
        assertThat(appToken.getAccount()).isNotNull();
        assertThat(appToken.getAccount().getEmail()).isEqualTo("user@user.com");
    }
}
