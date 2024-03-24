package com.fcastro.accountservice.accountgroup;

import com.fcastro.accountservice.account.AccountService;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.AccountGroupDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountGroups")
public class AccountGroupController {

    private final AccountService accountService;
    private final AccountGroupService service;

    public AccountGroupController(AccountService accountService, AccountGroupService accountGroupService) {
        this.accountService = accountService;
        this.service = accountGroupService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AccountGroupDto> get(@PathVariable long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroup not found"));
    }

    @GetMapping
    public ResponseEntity<List<AccountGroupDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<AccountGroupDto> create(@RequestBody AccountGroupDto newDto, HttpServletResponse response) {
        var group = service.save(newDto);

        //need to update cookie: add new group to the role
        var newCookie = accountService.updateCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @PutMapping("/{id}")
    ResponseEntity<AccountGroupDto> replace(@Valid @RequestBody AccountGroupDto newDto, @PathVariable Long id) {
        var dto = service.get(id)
                .map(resource -> {
                    resource.setName(newDto.getName());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroup not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<AccountGroupDto> delete(@PathVariable Long id, HttpServletResponse response) {
        service.delete(id);

        //need to update cookie: add new group to the role
        var newCookie = accountService.updateCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());

        return ResponseEntity.noContent().build();
    }

}
