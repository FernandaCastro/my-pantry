package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.account.AccountDto;
import com.fcastro.accountservice.account.AccountRepository;
import com.fcastro.accountservice.accountgroup.AccountGroupService;
import com.fcastro.accountservice.exception.AccountAlreadyExistsException;
import com.fcastro.accountservice.exception.PasswordAnswerNotMatchException;
import com.fcastro.accountservice.authentication.KeyPairDto;
import com.fcastro.accountservice.authentication.RSAUtil;
import com.fcastro.commons.config.MessageTranslator;
import com.fcastro.commons.exception.RequestParamExpectedException;
import com.fcastro.commons.exception.ResourceNotFoundException;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.exception.TokenVerifierException;
import com.fcastro.security.core.jwt.JWTHandler;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.opencensus.common.Duration;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final JWTHandler jwtHandler;
    private final PasswordEncoder passwordEncoder;
    private final GoogleIdTokenVerifier googleVerifier;
    private final SecurityPropertiesConfig securityProperties;
    private final RSAUtil rsaUtil;

    private final AccountRepository accountRepository;
    private final AccountGroupService accountGroupService;


    public AuthenticationService(AuthenticationManager authenticationManager, JWTHandler jwtHandler, PasswordEncoder passwordEncoder, GoogleIdTokenVerifier googleVerifier, SecurityPropertiesConfig securityProperties, RSAUtil rsaUtil, AccountRepository accountRepository, AccountGroupService accountGroupService) {
        this.authenticationManager = authenticationManager;
        this.jwtHandler = jwtHandler;
        this.passwordEncoder = passwordEncoder;
        this.googleVerifier = googleVerifier;
        this.securityProperties = securityProperties;
        this.rsaUtil = rsaUtil;
        this.accountRepository = accountRepository;
        this.accountGroupService = accountGroupService;
    }

    public Optional<AccountDto> getUser(String email) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException(MessageTranslator.getMessage("error.validation.email"));

        return accountRepository.findByEmail(email.toLowerCase()).map(this::convertToDto);
    }

    public KeyPairDto getPublicKey() {
        return rsaUtil.getPublicKey();
    }

    public AccountDto loginOAuthGoogle(IdTokenDto token) {
        Account account = verifyIDToken(token.getIdToken());

        return createOrUpdateUser(account);
    }

    /**
     * Handles the Account creation or update when logging through provider (Google)
     **/
    @Transactional
    private AccountDto createOrUpdateUser(Account account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail().toLowerCase()).orElse(null);

        if (existingAccount == null) {
            var createdAccount = accountRepository.save(account);
            accountGroupService.createParentGroup(createdAccount);
            return convertToDto(createdAccount);
        }

        existingAccount.setName(account.getName());
        existingAccount.setPictureUrl(account.getPictureUrl());
        accountRepository.save(existingAccount);
        return convertToDto(existingAccount);
    }

    public AccountDto resetPassword(AccountDto account) {

        if (account.getEmail() == null || account.getEmail().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.email"));
        }
        if (account.getPasswordAnswer() == null || account.getPasswordAnswer().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.resetAnswer"));
        }
        if (account.getPassword() == null || account.getPassword().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.password"));
        }

        Account existingAccount = accountRepository.findByEmail(account.getEmail().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var passwordAnswer = rsaUtil.decrypt(account.getPasswordAnswer());
        if (!passwordAnswer.equalsIgnoreCase(existingAccount.getPasswordAnswer()))
            throw new PasswordAnswerNotMatchException(MessageTranslator.getMessage("error.reset.answer.not.match"));

        var password = rsaUtil.decrypt(account.getPassword());
        existingAccount.setPassword(passwordEncoder.encode(password));
        accountRepository.save(existingAccount);
        return convertToDto(existingAccount);
    }

    /**
     * Handles only registration of an Account => not secured
     * When account does not exist: Create new account.
     * When account had already been created by a specific provider (Google): Store the password.
     */
    public AccountDto register(AccountDto newAccount) {

        if (newAccount.getEmail() == null || newAccount.getEmail().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.email"));
        }
        if (newAccount.getName() == null || newAccount.getName().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.name"));
        }
        if (newAccount.getPasswordQuestion() == null || newAccount.getPasswordQuestion().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.resetQuestion"));
        }
        if (newAccount.getPasswordAnswer() == null || newAccount.getPasswordAnswer().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.resetAnswer"));
        }

        String newPassword = rsaUtil.decrypt(newAccount.getPassword());
        String newPasswordAnswer = rsaUtil.decrypt(newAccount.getPasswordAnswer());

        //Account was created by a specific provider (Google): Store the password.
        Account existingAccount = accountRepository.findByEmail(newAccount.getEmail().toLowerCase()).orElse(null);
        if (existingAccount == null) {

            var account = Account.builder()
                    .name(newAccount.getName())
                    .email(newAccount.getEmail())
                    .password(passwordEncoder.encode(newPassword))
                    .passwordQuestion(newAccount.getPasswordQuestion())
                    .passwordAnswer(newPasswordAnswer)
                    .build();

            return saveAndCreateParentGroup(account);
        }

        //Account was created by a specific provider (Google): Store the password.
        if (Strings.isNotEmpty(existingAccount.getExternalProvider()) &&
                Strings.isEmpty(existingAccount.getPassword())) {

            existingAccount.setPassword(passwordEncoder.encode(newPassword));
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
            existingAccount.setPasswordAnswer(newPasswordAnswer);

            return saveAndCreateParentGroup(existingAccount);
        }

        //Account was pre-created by another user: Complete registration.
        if (Strings.isEmpty(existingAccount.getExternalProvider()) &&
                Strings.isEmpty(existingAccount.getPassword())) {

            existingAccount.setName(newAccount.getName());
            existingAccount.setPassword(passwordEncoder.encode(newPassword));
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
            existingAccount.setPasswordAnswer(newPasswordAnswer);

            return saveAndCreateParentGroup(existingAccount);
        }

        throw new AccountAlreadyExistsException(MessageTranslator.getMessage("error.email.already.in.use"));
    }

    private AccountDto saveAndCreateParentGroup(Account account) {
        account = accountRepository.save(account);
        accountGroupService.createParentGroup(account);
        return convertToDto(account);
    }

    private Account verifyIDToken(String token) {

        try {
            GoogleIdToken tokenObj = googleVerifier.verify(token);
            if (tokenObj == null) {
                throw new TokenVerifierException(MessageTranslator.getMessage("error.invalid.google.token"));
            }
            GoogleIdToken.Payload payload = tokenObj.getPayload();
            String externalProvider = "google";
            String name = (String) payload.get("name");
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");

            return new Account(externalProvider, name, email, pictureUrl);

        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error(e.getMessage());
            throw new TokenVerifierException(MessageTranslator.getMessage("error.google.token.verifier"));
        }
    }

    public AccountDto login(String email, String password) {

        Account account = accountRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        String decryptedPassword = rsaUtil.decrypt(password);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, decryptedPassword));

//        if (!authentication.isAuthenticated())
//            throw  ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return convertToDto(account);
    }

    public ResponseCookie generateCookie(AccountDto accountDto, boolean rememberMe) {
        var jwtToken = createJwtToken(accountDto, rememberMe);
        return createCookie(jwtToken, rememberMe);
    }

    private ResponseCookie createCookie(String jwtToken, boolean rememberMe) {

        var interval = rememberMe ? JWTHandler.TOKEN_VALIDITY_REMEMBER : JWTHandler.TOKEN_VALIDITY;

        return ResponseCookie.from("AUTH-TOKEN", jwtToken)
                .httpOnly(true)
                .maxAge(Duration.fromMillis(interval).getSeconds())
                .path("/")
                .secure(securityProperties.isHttps())  //true= HTTPS only
                .build();
    }

    private String createJwtToken(AccountDto accountDto, boolean rememberMe) {
        return jwtHandler.createToken(accountDto.getEmail(), rememberMe);
    }

    private AccountDto convertToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .pictureUrl(account.getPictureUrl())
                .passwordQuestion(account.getPasswordQuestion())
                .theme(account.getTheme())
                .build();
    }
}
