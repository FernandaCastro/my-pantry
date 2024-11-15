package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.security.KeyPairDto;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.RequestParamExpectedException;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.AccountDto;
import com.fcastro.security.core.model.IdTokenDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/accountservice/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/public-key")
    public ResponseEntity<KeyPairDto> getPublicKey() {
        return ResponseEntity.ok(authenticationService.getPublicKey());
    }

    @PostMapping("/google-login")
    public ResponseEntity<AccountDto> loginWithGoogle(@RequestBody IdTokenDto request, boolean rememberMe, HttpServletResponse response) {

        var account = authenticationService.loginOAuthGoogle(request);

        var cookie = authenticationService.generateCookie(account, rememberMe);

        //Using includeCredential=true. When SameSite=None and Secure=true all cookies will be included anyway.
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(account);
    }

    @GetMapping("/user-info")
    public ResponseEntity<AccountDto> getUserInfo(Principal principal) {

        return authenticationService.getUser(principal.getName())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));
    }

    @PostMapping("/login")
    public ResponseEntity<AccountDto> login(@RequestBody AccountDto request, @RequestParam boolean rememberMe, HttpServletResponse response) {

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.email"));
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.validation.password"));
        }

//        try {
        var account = authenticationService.login(request.getEmail(), request.getPassword());

        var cookie = authenticationService.generateCookie(account, rememberMe);

            //Using includeCredential=true. When SameSite=None and Secure=true all cookies will be included anyway.
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(account);
//
//        } catch (AuthenticationException | KeyPairException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();
//        }
    }

    @PostMapping("/register")
    public ResponseEntity<AccountDto> register(@RequestBody AccountDto request) {

        var account = authenticationService.register(request);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<AccountDto> getResetPasswordQuestion(@RequestParam String email) {
        var account = authenticationService.getUser(email)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));

        var response = AccountDto.builder()
                .email(account.getEmail())
                .passwordQuestion(account.getPasswordQuestion())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AccountDto> resetPassword(@RequestBody AccountDto request) {

        var account = authenticationService.resetPassword(request);
        return ResponseEntity.ok(account);
    }
}
