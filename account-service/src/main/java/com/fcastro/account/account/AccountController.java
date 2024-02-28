package com.fcastro.account.account;

import com.fcastro.account.exception.ResourceNotFoundException;
import com.fcastro.security.model.AccountDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService userService) {
        this.service = userService;
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAll(@RequestParam String searchParam) {
        return ResponseEntity.ok(service.getAll(searchParam));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewAccountDto> get(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping
    public ResponseEntity<AccountDto> create(@Valid @RequestBody AccountDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.preCreateAccount(newDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<AccountDto> replace(@Valid @RequestBody NewAccountDto newAccount, @PathVariable Long id) {

        var dto = service.updateAccount(newAccount);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

}
