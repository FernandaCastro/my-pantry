package com.fcastro.account.account;

import com.fcastro.account.exception.ResourceNotFoundException;
import com.fcastro.model.AccountDto;
import com.fcastro.security.model.IdTokenDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/oauth")
public class AccountController {

    private final AccountService userService;

    @Value("${app.allowed-origin}")
    private String allowedOrigin;

    public AccountController(AccountService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AccountDto> loginWithGoogle(@RequestBody IdTokenDto requestBody, HttpServletResponse response) {
        var appToken = userService.loginOAuthGoogle(requestBody);
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", appToken.getToken())
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)  //true= HTTPS only
                .build();
        //Using includeCredential=true. When SameSite=None and Secure=true all cookies will be included anyway.
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(appToken.getAccount());
    }

    @GetMapping("/user-info")
    public ResponseEntity<AccountDto> getUserInfo(Principal principal) {
        return userService.getUser(Long.valueOf(principal.getName()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
