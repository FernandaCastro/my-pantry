package com.fcastro.account.account;

import com.fcastro.account.exception.ResourceNotFoundException;
import com.fcastro.app.model.AccountDto;
import com.fcastro.security.model.IdTokenDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService userService) {
        this.service = userService;
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<AccountDto> loginWithGoogle(@RequestBody IdTokenDto requestBody, HttpServletResponse response) {
        var appToken = service.loginOAuthGoogle(requestBody);
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

    @GetMapping("/oauth/user-info")
    public ResponseEntity<AccountDto> getUserInfo(Principal principal) {
        return service.getUser(Long.valueOf(principal.getName()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAll(@RequestParam String searchParam) {
        return ResponseEntity.ok(service.getAll(searchParam));
    }

}
