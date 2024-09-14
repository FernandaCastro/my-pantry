package com.fcastro.accountservice.security;

import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.account.AccountRepository;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.security.core.handler.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var roles = new ArrayList<String>();

        return UserDetailsImpl.build(account.getEmail(), account.getPassword(), roles);
    }

}
