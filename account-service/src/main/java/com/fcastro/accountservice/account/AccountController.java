package com.fcastro.accountservice.account;

import com.fcastro.commons.config.MessageTranslator;
import com.fcastro.commons.exception.ResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
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

    @PutMapping("/{id}/theme")
    ResponseEntity<AccountDto> updateThema(@PathVariable Long id, @RequestParam @NotNull String theme) {

        var dto = service.updateAccountTheme(id, theme);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AccountDto> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
