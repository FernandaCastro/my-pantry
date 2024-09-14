package com.fcastro.accountservice.account;

import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.AccountDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountservice/accounts")
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
    public ResponseEntity<AccountDto> get(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.email.not.found")));
    }

    @PostMapping
    public ResponseEntity<AccountDto> preCreate(@RequestBody AccountDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.preCreateAccount(newDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<AccountDto> replace(@RequestBody AccountDto newAccount, @PathVariable Long id) {

        var dto = service.updateAccount(newAccount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

}
