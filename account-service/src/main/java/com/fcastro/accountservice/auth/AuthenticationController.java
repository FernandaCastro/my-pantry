package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.account.AccountService;
import com.fcastro.accountservice.account.LogintDto;
import com.fcastro.accountservice.account.NewAccountDto;
import com.fcastro.accountservice.account.ResetPasswordDto;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.handler.UserDetailsImpl;
import com.fcastro.security.core.model.AccountDto;
import com.fcastro.security.core.model.IdTokenDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
@RequestMapping("auth")
public class AuthenticationController {

    private final AccountService service;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AccountService userService, AuthenticationManager authenticationManager) {
        this.service = userService;
        this.authenticationManager = authenticationManager;
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
    public ResponseEntity<AccountDto> login(@Valid @RequestBody LogintDto request, HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            if (!authentication.isAuthenticated())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();

            SecurityContextHolder.getContext().setAuthentication(authentication);

            var appToken = service.login(((UserDetailsImpl) authentication.getPrincipal()).getUsername());
            var cookie = service.createCookie(appToken.getToken());

            //Using includeCredential=true. When SameSite=None and Secure=true all cookies will be included anyway.
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(appToken.getAccount());

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AccountDto> register(@Valid @RequestBody NewAccountDto request) {

        var account = service.register(request);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ResetPasswordDto> getResetPasswordQuestion(@RequestParam String email) {
        var account = service.getUser(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var response = ResetPasswordDto.builder()
                .email(account.getEmail())
                .passwordQuestion(account.getPasswordQuestion())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AccountDto> resetPassword(@Valid @RequestBody ResetPasswordDto request) {

        var account = service.resetPassword(request);
        return ResponseEntity.ok(account);
    }
}
