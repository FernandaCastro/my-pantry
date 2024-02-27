package com.fcastro.account.account;

import com.fcastro.security.model.AccountDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService userService) {
        this.service = userService;
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAll(@RequestParam String searchParam) {
        return ResponseEntity.ok(service.getAll(searchParam));
    }

}
