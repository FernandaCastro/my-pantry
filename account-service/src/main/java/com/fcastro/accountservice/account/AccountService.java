package com.fcastro.accountservice.account;

import com.fcastro.accountservice.accountgroup.AccountGroupService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.accountservice.exception.AccountAlreadyExistsException;
import com.fcastro.accountservice.exception.PasswordAnswerNotMatchException;
import com.fcastro.accountservice.security.RSAUtil;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.RequestParamExpectedException;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.exception.TokenVerifierException;
import com.fcastro.security.core.jwt.JWTHandler;
import com.fcastro.security.core.model.AccountDto;
import com.fcastro.security.core.model.AppTokenDto;
import com.fcastro.security.core.model.IdTokenDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private final JWTHandler jwtHandler;
    private final PasswordEncoder passwordEncoder;
    private final GoogleIdTokenVerifier googleVerifier;
    private final SecurityPropertiesConfig securityProperties;
    private final RSAUtil rsaUtil;

    private final AccountRepository accountRepository;
    private final AccountGroupService accountGroupService;
    private final AccountGroupMemberService accountGroupMemberService;

    public AccountService(AccountRepository userRepository, JWTHandler jwtHandler, GoogleIdTokenVerifier googleVerifier, AccountGroupService accountGroupService, AccountGroupMemberService accountGroupMemberService, PasswordEncoder passwordEncoder, SecurityPropertiesConfig securityProperties, RSAUtil rsaUtil) {
        this.accountRepository = userRepository;
        this.jwtHandler = jwtHandler;
        this.googleVerifier = googleVerifier;
        this.accountGroupService = accountGroupService;
        this.accountGroupMemberService = accountGroupMemberService;
        this.passwordEncoder = passwordEncoder;
        this.securityProperties = securityProperties;
        this.rsaUtil = rsaUtil;
    }

    public Optional<AccountDto> get(Long id) {
        return accountRepository.findById(id).map(this::convertToDto);
    }

    public Optional<AccountDto> getUser(String email) {
        return accountRepository.findByEmail(email).map(this::convertToDto);
    }

    public AppTokenDto loginOAuthGoogle(IdTokenDto token) {
        Account account = verifyIDToken(token.getIdToken());

        var accountDto = createOrUpdateUser(account);
        var jwtToken = createJwtToken(accountDto);

        return AppTokenDto.builder()
                .token(jwtToken)
                .account(accountDto)
                .build();
    }

    public AppTokenDto login(String email) {

        var accountFound = accountRepository.findByEmail(email)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var jwtToken = createJwtToken(accountFound);
        var accountDto = AccountDto.builder()
                .id(accountFound.getId())
                .name(accountFound.getName())
                .email(accountFound.getEmail())
                .pictureUrl(accountFound.getPictureUrl())
                .build();

        return AppTokenDto.builder()
                .token(jwtToken)
                .account(accountDto)
                .build();
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

    /**
     * Handles the Account creation or update when logging through provider (Google)
     **/
    @Transactional
    private AccountDto createOrUpdateUser(Account account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail()).orElse(null);

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

        Account existingAccount = accountRepository.findByEmail(account.getEmail())
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
        Account existingAccount = accountRepository.findByEmail(newAccount.getEmail()).orElse(null);
        if (existingAccount == null) {
            var account = Account.builder()
                    .name(newAccount.getName())
                    .email(newAccount.getEmail())
                    .password(passwordEncoder.encode(newPassword))
                    .passwordQuestion(newAccount.getPasswordQuestion())
                    .passwordAnswer(newPasswordAnswer)
                    .build();

            account = accountRepository.save(account);
            accountGroupService.createParentGroup(account);
            return convertToDto(account);
        }

        //Account was created by a specific provider (Google): Store the password.
        if (Strings.isNotEmpty(existingAccount.getExternalProvider()) &&
                Strings.isEmpty(existingAccount.getPassword())) {
            existingAccount.setPassword(passwordEncoder.encode(newPassword));
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
            existingAccount.setPasswordAnswer(newPasswordAnswer);

            existingAccount = accountRepository.save(existingAccount);
            accountGroupService.createParentGroup(existingAccount);

            return convertToDto(existingAccount);
        }

        //Account was pre-created by another user: Complete registration.
        if (Strings.isEmpty(existingAccount.getExternalProvider()) &&
                Strings.isEmpty(existingAccount.getPassword())) {
            existingAccount.setName(newAccount.getName());
            existingAccount.setPassword(passwordEncoder.encode(newPassword));
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
            existingAccount.setPasswordAnswer(newPasswordAnswer);

            existingAccount = accountRepository.save(existingAccount);
            accountGroupService.createParentGroup(existingAccount);

            return convertToDto(existingAccount);
        }

        throw new AccountAlreadyExistsException(MessageTranslator.getMessage("error.email.already.in.use"));
    }

    /**
     * Handles the update of an existing Account => Secured by a JWT
     **/
    public AccountDto updateAccount(AccountDto newAccount) {

        Account existingAccount = accountRepository.findById(newAccount.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.account.not.found")));


        if (newAccount.getPassword() != null && !newAccount.getPassword().isEmpty()) {
            //Base-case assumes password has not changed
            String password = existingAccount.getPassword();
            String newPassword = rsaUtil.decrypt(newAccount.getPassword());

            //In case password has changed, encode it again
            if (!newPassword.equals(password) &&
                    !passwordEncoder.matches(newPassword, password)) {
                password = passwordEncoder.encode(newPassword);
            }

            existingAccount.setPassword(password);
        }

        if (newAccount.getName() != null && !newAccount.getName().isEmpty()) {
            existingAccount.setName(newAccount.getName());
        }
        if (newAccount.getEmail() != null && !newAccount.getEmail().isEmpty()) {
            existingAccount.setEmail(newAccount.getEmail());
        }
        if (newAccount.getPasswordQuestion() != null && !newAccount.getPasswordQuestion().isEmpty()) {
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
        }
        if (newAccount.getPasswordAnswer() != null && !newAccount.getPasswordAnswer().isEmpty()) {
            existingAccount.setPasswordAnswer(rsaUtil.decrypt(newAccount.getPasswordAnswer()));
        }

        var updatedAccount = accountRepository.save(existingAccount);
        return convertToDto(updatedAccount);
    }

    /**
     * Handles a pre registration of a new account, made by another user => secured by JWT
     **/
    public AccountDto preCreateAccount(AccountDto newAccount) {
        if (newAccount.getEmail() == null || newAccount.getEmail().isBlank() ||
                newAccount.getName() == null || newAccount.getName().isBlank())
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.email.and.name.required"));

        Account existingAccount = accountRepository.findByEmail(newAccount.getEmail()).orElse(null);
        if (existingAccount == null) {
            var account = Account.builder()
                    .name(newAccount.getName())
                    .email(newAccount.getEmail())
                    .build();

            account = accountRepository.save(account);
            return convertToDto(account);
        }
        throw new AccountAlreadyExistsException(MessageTranslator.getMessage("error.pre.create.email.already.in.use"));
    }

    public List<AccountDto> getAll(String searchParam) {

        if (searchParam == null)
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.search.param.required"));

        var accountList = accountRepository.findAllByNameOrEmail(searchParam.toLowerCase());
        return accountList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private String createJwtToken(AccountDto accountDto) {
        return jwtHandler.createToken(accountDto.getEmail(), true);
    }

    public ResponseCookie createCookie(String jwtToken) {
        return ResponseCookie.from("AUTH-TOKEN", jwtToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(securityProperties.isHttps())  //true= HTTPS only
                .build();
    }


    private AccountDto convertToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .pictureUrl(account.getPictureUrl())
                .passwordQuestion(account.getPasswordQuestion())
                .build();
    }
}
