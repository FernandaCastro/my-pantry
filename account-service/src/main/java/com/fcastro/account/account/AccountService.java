package com.fcastro.account.account;

import com.fcastro.account.accountGroup.AccountGroupService;
import com.fcastro.account.exception.AccountAlreadyExistsException;
import com.fcastro.account.exception.PasswordAnswerNotMatchException;
import com.fcastro.account.exception.ResourceNotFoundException;
import com.fcastro.app.exception.RequestParamExpectedException;
import com.fcastro.security.exception.TokenVerifierException;
import com.fcastro.security.jwt.JWTHandler;
import com.fcastro.security.jwt.SecurityConfigData;
import com.fcastro.security.model.AccountDto;
import com.fcastro.security.model.AppTokenDto;
import com.fcastro.security.model.IdTokenDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final GoogleIdTokenVerifier googleVerifier;
    private final AuthenticationManager authenticationManager;
    private final SecurityConfigData securityConfigData;
    private final AccountGroupService accountGroupService;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository userRepository, JWTHandler jwtHandler, AuthenticationManager authenticationManager, SecurityConfigData securityConfigData, AccountGroupService accountGroupService, PasswordEncoder passwordEncoder) {
        this.accountRepository = userRepository;
        this.jwtHandler = jwtHandler;
        this.authenticationManager = authenticationManager;
        this.securityConfigData = securityConfigData;
        this.accountGroupService = accountGroupService;
        this.passwordEncoder = passwordEncoder;

        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        googleVerifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(securityConfigData.getGoogleClientId()))
                .build();
    }

    public Optional<NewAccountDto> get(Long id) {
        return accountRepository.findById(id).map(this::convertToNewAccountDto);
    }

    public Optional<AccountDto> getUser(String email) {
        return accountRepository.findByEmail(email).map(this::convertToDto);
    }

    public AppTokenDto loginOAuthGoogle(IdTokenDto token) {
        Account account = verifyIDToken(token.getIdToken());
        if (account == null) {
            throw new TokenVerifierException("");
        }

        var accounDto = createOrUpdateUser(account);

        var appToken = jwtHandler.createToken(accounDto, false);

        return AppTokenDto.builder()
                .token(appToken)
                .account(accounDto)
                .build();
    }

    public AppTokenDto login(String email) {

        var accountDto = accountRepository.findByEmail(email)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ;

        var jwtToken = jwtHandler.createToken(accountDto, false);

        return AppTokenDto.builder()
                .token(jwtToken)
                .account(accountDto)
                .build();
    }

    private Account verifyIDToken(String token) {
        try {
            GoogleIdToken tokenObj = googleVerifier.verify(token);
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

    /**
     * Handles the Account creation or update when logging through provider (Google)
     **/
    @Transactional
    private AccountDto createOrUpdateUser(Account account) {
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

    public AccountDto resetPassword(ResetPasswordDto account) {
        Account existingAccount = accountRepository.findByEmail(account.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!account.getPasswordAnswer().toLowerCase().equals(existingAccount.getPasswordAnswer().toLowerCase()))
            throw new PasswordAnswerNotMatchException("Password reset failed. Answer does not match.");

        existingAccount.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(existingAccount);
        return convertToDto(existingAccount);
    }

    /**
     * Handles only registration of an Account => not secured
     * Account does not exist: Create new
     * Account was created by a specific provider (Google): Store the password.
     */
    public AccountDto register(NewAccountDto newAccount) {

        //Account was created by a specific provider (Google): Store the password.
        Account existingAccount = accountRepository.findByEmail(newAccount.getEmail()).orElse(null);
        if (existingAccount == null) {
            var account = Account.builder()
                    .name(newAccount.getName())
                    .email(newAccount.getEmail())
                    .password(passwordEncoder.encode(newAccount.getPassword()))
                    .roles("ROLE_USER")
                    .passwordQuestion(newAccount.getPasswordQuestion())
                    .passwordAnswer(newAccount.getPasswordAnswer())
                    .build();

            account = accountRepository.save(account);
            accountGroupService.createDefaultGroup(account.getId());
            return convertToDto(account);
        }

        //Account was created by a specific provider (Google): Store the password.
        if (Strings.isNotEmpty(existingAccount.getExternalProvider()) &&
                Strings.isEmpty(existingAccount.getPassword())) {
            existingAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
            existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
            existingAccount.setPasswordAnswer(newAccount.getPasswordAnswer());

            existingAccount = accountRepository.save(existingAccount);
            accountGroupService.createDefaultGroup(existingAccount.getId());

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
            accountGroupService.createDefaultGroup(existingAccount.getId());

            return convertToDto(existingAccount);
        }

        throw new AccountAlreadyExistsException("This email is already in use. Please login using your password.");
    }

    /**
     * Handles the update of an existing Account => Secured by a JWT
     **/
    public AccountDto updateAccount(NewAccountDto newAccount) {

        Account existingAccount = accountRepository.findById(newAccount.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        //Base-case assumes password has not changed
        String password = existingAccount.getPassword();

        //In case password has changed, encode it again
        if (!password.equals(newAccount.getPassword()) &&
                !passwordEncoder.matches(newAccount.getPassword(), password)) {
            password = passwordEncoder.encode(newAccount.getPassword());
        }

        existingAccount.setName(newAccount.getName());
        existingAccount.setEmail(newAccount.getEmail());
        existingAccount.setPassword(password);
        existingAccount.setPasswordQuestion(newAccount.getPasswordQuestion());
        existingAccount.setPasswordAnswer(newAccount.getPasswordAnswer());

        existingAccount = accountRepository.save(existingAccount);
        return convertToDto(existingAccount);
    }

    /**
     * Handles a pre registration of a new account, made by another user => secured by JWT
     **/
    public AccountDto preCreateAccount(AccountDto newAccount) {
        Account existingAccount = accountRepository.findByEmail(newAccount.getEmail()).orElse(null);
        if (existingAccount == null) {
            var account = Account.builder()
                    .email(newAccount.getEmail())
                    .roles("ROLE_USER")
                    .build();

            account = accountRepository.save(account);
            return convertToDto(account);
        }
        throw new AccountAlreadyExistsException("There is an account using this email.");
    }


    public List<AccountDto> getAll(String searchParam) {

        if (searchParam == null)
            throw new RequestParamExpectedException("Expecting to receive SearchParam: name or email value");

        var accountList = accountRepository.findAllByNameOrEmail(searchParam.toLowerCase());
        return accountList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private AccountDto convertToDto(NewAccountDto account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .pictureUrl(account.getPictureUrl())
                .roles(account.getRoles())
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
                .roles(account.getRoles())
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
                .roles(account.getRoles())
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
                .roles(accountDto.getRoles())
                .build();
    }
}
