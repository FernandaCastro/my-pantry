package com.fcastro.account.account;

import com.fcastro.account.accountGroup.AccountGroupService;
import com.fcastro.app.exception.RequestParamExpectedException;
import com.fcastro.app.model.AccountDto;
import com.fcastro.security.config.JWTHandler;
import com.fcastro.security.config.SecurityConfigData;
import com.fcastro.security.exception.TokenVerifierException;
import com.fcastro.security.model.AppTokenDto;
import com.fcastro.security.model.IdTokenDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final JWTHandler jwtHandler;
    private final GoogleIdTokenVerifier verifier;
    private final SecurityConfigData securityConfigData;
    private final AccountGroupService accountGroupService;

    public AccountService(AccountRepository userRepository, JWTHandler jwtHandler, SecurityConfigData securityConfigData, AccountGroupService accountGroupService) {
        this.accountRepository = userRepository;
        this.jwtHandler = jwtHandler;
        this.securityConfigData = securityConfigData;
        this.accountGroupService = accountGroupService;

        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(securityConfigData.getGoogleClientId()))
                .build();
    }

    public Optional<AccountDto> getUser(Long id) {
        return accountRepository.findById(id).map(this::convertToDto);
    }

    public AppTokenDto loginOAuthGoogle(IdTokenDto token) {
        Account account = verifyIDToken(token.getIdToken());
        if (account == null) {
            throw new TokenVerifierException("");
        }

        var accountDto = createOrUpdateUser(account);
        var appToken = jwtHandler.createToken(accountDto, false);

        return AppTokenDto.builder()
                .token(appToken)
                .account(accountDto)
                .build();
    }

    private Account verifyIDToken(String token) {
        try {
            GoogleIdToken tokenObj = verifier.verify(token);
            if (tokenObj == null) {
                throw new TokenVerifierException("Invalid Google Token");
            }
            GoogleIdToken.Payload payload = tokenObj.getPayload();
            String externalProvider = "google";
            String name = (String) payload.get("name");
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");

            return new Account(externalProvider, name, email, pictureUrl);
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error(e.getMessage());
            throw new TokenVerifierException(e.getMessage());
        }
    }

    @Transactional
    public AccountDto createOrUpdateUser(Account account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (existingAccount == null) {
            account.setRoles("ROLE_USER");

            var createdAccount = accountRepository.save(account);

            accountGroupService.createDefaultGroup(createdAccount.getId());

            return convertToDto(account);
        }
        existingAccount.setName(account.getName());
        existingAccount.setPictureUrl(account.getPictureUrl());
        accountRepository.save(existingAccount);
        return convertToDto(existingAccount);
    }

    public List<AccountDto> getAll(String searchParam) {

        if (searchParam == null)
            throw new RequestParamExpectedException("Expecting to receive SearchParam: name or email value");

        var accountList = accountRepository.findAllByNameOrEmail(searchParam.toLowerCase());
        return accountList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private AccountDto convertToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .pictureUrl(account.getPictureUrl())
                .roles(account.getRoles())
                .build();
    }
}
