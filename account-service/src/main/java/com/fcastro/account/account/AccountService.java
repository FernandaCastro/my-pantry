package com.fcastro.account.account;

import com.fcastro.model.AccountDto;
import com.fcastro.security.config.JWTHandler;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository userRepository;
    private final JWTHandler jwtHandler;
    private final GoogleIdTokenVerifier verifier;

    public AccountService(@Value("${google.clientId}") String clientId, AccountRepository userRepository, JWTHandler jwtHandler) {
        this.userRepository = userRepository;
        this.jwtHandler = jwtHandler;

        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public Optional<AccountDto> getUser(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
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
        Account existingAccount = userRepository.findByEmail(account.getEmail()).orElse(null);
        if (existingAccount == null) {
            account.setRoles("ROLE_USER");
            userRepository.save(account);
            return convertToDto(account);
        }
        existingAccount.setName(account.getName());
        existingAccount.setPictureUrl(account.getPictureUrl());
        userRepository.save(existingAccount);
        return convertToDto(existingAccount);
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
