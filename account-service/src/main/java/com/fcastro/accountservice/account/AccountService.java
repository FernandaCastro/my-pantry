package com.fcastro.accountservice.account;

import com.fcastro.accountservice.accountgroup.AccountGroupService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.accountservice.exception.AccountAlreadyExistsException;
import com.fcastro.accountservice.exception.PasswordAnswerNotMatchException;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.RequestParamExpectedException;
import com.fcastro.app.exception.ResourceNotFoundException;
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
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final AccountRepository accountRepository;
    private final AccountGroupService accountGroupService;
    private final AccountGroupMemberService accountGroupMemberService;

    public AccountService(AccountRepository userRepository, JWTHandler jwtHandler, GoogleIdTokenVerifier googleVerifier, AccountGroupService accountGroupService, AccountGroupMemberService accountGroupMemberService, PasswordEncoder passwordEncoder) {
        this.accountRepository = userRepository;
        this.jwtHandler = jwtHandler;
        this.googleVerifier = googleVerifier;
        this.accountGroupService = accountGroupService;
        this.accountGroupMemberService = accountGroupMemberService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<NewAccountDto> get(Long id) {
        return accountRepository.findById(id).map(this::convertToNewAccountDto);
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

        var accountDto = accountRepository.findByEmail(email)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var jwtToken = createJwtToken(accountDto);

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
            throw new TokenVerifierException(e.getMessage());
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

    public AccountDto resetPassword(ResetPasswordDto account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        if (!account.getPasswordAnswer().equalsIgnoreCase(existingAccount.getPasswordAnswer()))
            throw new PasswordAnswerNotMatchException(MessageTranslator.getMessage("error.reset.answer.not.match"));

        existingAccount.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(existingAccount);
        return convertToDto(existingAccount);
    }

    /**
     * Handles only registration of an Account => not secured
     * When account does not exist: Create new account.
     * When account had already been created by a specific provider (Google): Store the password.
     */
    public AccountDto register(NewAccountDto newAccount) {

        //Account was created by a specific provider (Google): Store the password.
        Account existingAccount = accountRepository.findByEmail(newAccount.getEmail()).orElse(null);
        if (existingAccount == null) {
            var account = Account.builder()
                    .name(newAccount.getName())
                    .email(newAccount.getEmail())
                    .password(passwordEncoder.encode(newAccount.getPassword()))
                    .passwordQuestion(newAccount.getPasswordQuestion())
                    .passwordAnswer(newAccount.getPasswordAnswer())
                    .build();

            account = accountRepository.save(account);
            accountGroupService.createParentGroup(account);
            return convertToDto(account);
        }

        //Account was created by a specific provider (Google): Store the password.
        if (Strings.isNotEmpty(existingAccount.getExternalProvider()) &&
                Strings.isEmpty(existingAccount.getPassword())) {
            existingAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
            existingAccount.setPasswordAnswer(newAccount.getPasswordAnswer());

            existingAccount = accountRepository.save(existingAccount);
            accountGroupService.createParentGroup(existingAccount);

            return convertToDto(existingAccount);
        }

        //Account was pre-created by another user: Complete registration.
        if (Strings.isEmpty(existingAccount.getExternalProvider()) &&
                Strings.isEmpty(existingAccount.getPassword())) {
            existingAccount.setName(newAccount.getName());
            existingAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
            existingAccount.setPasswordAnswer(newAccount.getPasswordAnswer());

            existingAccount = accountRepository.save(existingAccount);
            accountGroupService.createParentGroup(existingAccount);

            return convertToDto(existingAccount);
        }

        throw new AccountAlreadyExistsException(MessageTranslator.getMessage("error.email.already.in.use"));
    }

    /**
     * Handles the update of an existing Account => Secured by a JWT
     **/
    public AccountDto updateAccount(NewAccountDto newAccount) {

        Account existingAccount = accountRepository.findById(newAccount.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.account.not.found")));

        //Base-case assumes password has not changed
        String password = existingAccount.getPassword();

        //In case password has changed, encode it again
        if (!newAccount.getPassword().equals(password) &&
                !passwordEncoder.matches(newAccount.getPassword(), password)) {
            password = passwordEncoder.encode(newAccount.getPassword());
        }

        existingAccount.setName(newAccount.getName());
        existingAccount.setEmail(newAccount.getEmail());
        existingAccount.setPassword(password);
        existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
        existingAccount.setPasswordAnswer(newAccount.getPasswordAnswer());

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

    //Updates cookie with new jwtToken containing updated accountGroups as role
    public ResponseCookie updateCookie() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var accountDto = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var jwtToken = createJwtToken(convertToDto(accountDto));
        return createCookie(jwtToken);
    }

    private String createJwtToken(AccountDto accountDto) {
        return jwtHandler.createToken(accountDto.getEmail(), true);
    }

    public ResponseCookie createCookie(String jwtToken) {
        return ResponseCookie.from("AUTH-TOKEN", jwtToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)  //true= HTTPS only
                .build();
    }

    private AccountDto convertToDto(NewAccountDto account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .pictureUrl(account.getPictureUrl())
                .password(account.getPassword())
                .passwordQuestion(account.getPasswordQuestion())
                .passwordAnswer(account.getPasswordAnswer())
                .build();
    }

    private NewAccountDto convertToNewAccountDto(Account account) {
        return NewAccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .pictureUrl(account.getPictureUrl())
                .password(account.getPassword())
                .passwordQuestion(account.getPasswordQuestion())
                .passwordAnswer(account.getPasswordAnswer())
                .build();
    }

    private AccountDto convertToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .pictureUrl(account.getPictureUrl())
                .password(account.getPassword())
                .passwordQuestion(account.getPasswordQuestion())
                .passwordAnswer(account.getPasswordAnswer())
                .build();
    }

    private Account convertToEntity(AccountDto accountDto) {
        return Account.builder()
                .id(accountDto.getId())
                .name(accountDto.getName())
                .email(accountDto.getEmail())
                .password(accountDto.getPassword())
                .passwordQuestion(accountDto.getPasswordQuestion())
                .passwordAnswer(accountDto.getPasswordAnswer())
                .pictureUrl(accountDto.getPictureUrl())
                .build();
    }
}
