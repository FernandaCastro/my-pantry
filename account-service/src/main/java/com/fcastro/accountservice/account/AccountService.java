package com.fcastro.accountservice.account;

import com.fcastro.accountservice.accountgroup.AccountGroupService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.accountservice.exception.AccountAlreadyExistsException;
import com.fcastro.accountservice.security.RSAUtil;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.RequestParamExpectedException;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.AccountDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

//    private final JWTHandler jwtHandler;
//    private final GoogleIdTokenVerifier googleVerifier;
//    private final SecurityPropertiesConfig securityProperties;

    private final PasswordEncoder passwordEncoder;
    private final RSAUtil rsaUtil;

    private final AccountRepository accountRepository;
    private final AccountGroupService accountGroupService;
    private final AccountGroupMemberService accountGroupMemberService;

    //public AccountService(AccountRepository userRepository, JWTHandler jwtHandler, GoogleIdTokenVerifier googleVerifier, AccountGroupService accountGroupService, AccountGroupMemberService accountGroupMemberService, PasswordEncoder passwordEncoder, SecurityPropertiesConfig securityProperties, RSAUtil rsaUtil) {
    public AccountService(PasswordEncoder passwordEncoder, RSAUtil rsaUtil, AccountRepository userRepository, AccountGroupService accountGroupService, AccountGroupMemberService accountGroupMemberService) {

        this.passwordEncoder = passwordEncoder;
        this.rsaUtil = rsaUtil;

        this.accountRepository = userRepository;
        this.accountGroupService = accountGroupService;
        this.accountGroupMemberService = accountGroupMemberService;

//        this.jwtHandler = jwtHandler;
//        this.googleVerifier = googleVerifier;
//        this.securityProperties = securityProperties;
    }

    public Optional<AccountDto> get(Long id) {
        return accountRepository.findById(id).map(this::convertToDto);
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
