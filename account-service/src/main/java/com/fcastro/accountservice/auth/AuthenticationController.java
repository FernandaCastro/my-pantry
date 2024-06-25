package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.account.AccountService;
import com.fcastro.accountservice.exception.KeyPairException;
import com.fcastro.accountservice.security.KeyPairDto;
import com.fcastro.accountservice.security.RSAUtil;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.RequestParamExpectedException;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.handler.UserDetailsImpl;
import com.fcastro.security.core.model.AccountDto;
import com.fcastro.security.core.model.IdTokenDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/accountservice/auth")
public class AuthenticationController {

    private final AccountService service;
    private final AuthenticationManager authenticationManager;
    private final RSAUtil rsaUtil;

    public AuthenticationController(AccountService userService, AuthenticationManager authenticationManager, RSAUtil rsaUtil) {
        this.service = userService;
        this.authenticationManager = authenticationManager;
        this.rsaUtil = rsaUtil;
    }

    @GetMapping("/public-key")
    public ResponseEntity<KeyPairDto> getPublicKey() {
        return ResponseEntity.ok(rsaUtil.getPublicKey());
    }

    @PostMapping("/google-login")
    public ResponseEntity<AccountDto> loginWithGoogle(@RequestBody IdTokenDto request, HttpServletResponse response) {
        var appToken = service.loginOAuthGoogle(request);
        var cookie = service.createCookie(appToken.getToken());

        //Using includeCredential=true. When SameSite=None and Secure=true all cookies will be included anyway.
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(appToken.getAccount());
    }

    @GetMapping("/user-info")
    public ResponseEntity<AccountDto> getUserInfo(Principal principal) {
        return service.getUser(principal.getName())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping("/login")
    public ResponseEntity<AccountDto> login(@RequestBody AccountDto request, HttpServletResponse response) {

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.email"));
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.password"));
        }

        try {
            String decryptedPassword = rsaUtil.decrypt(request.getPassword());

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), decryptedPassword));

            if (!authentication.isAuthenticated())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();

            SecurityContextHolder.getContext().setAuthentication(authentication);

            var appToken = service.login(((UserDetailsImpl) authentication.getPrincipal()).getUsername());
            var cookie = service.createCookie(appToken.getToken());

            //Using includeCredential=true. When SameSite=None and Secure=true all cookies will be included anyway.
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(appToken.getAccount());

        } catch (AuthenticationException | KeyPairException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AccountDto> register(@RequestBody AccountDto request) {

        var account = service.register(request);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<AccountDto> getResetPasswordQuestion(@RequestParam String email) {
        var account = service.getUser(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var response = AccountDto.builder()
                .email(account.getEmail())
                .passwordQuestion(account.getPasswordQuestion())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AccountDto> resetPassword(@RequestBody AccountDto request) {

        var account = service.resetPassword(request);
        return ResponseEntity.ok(account);
    }
}
