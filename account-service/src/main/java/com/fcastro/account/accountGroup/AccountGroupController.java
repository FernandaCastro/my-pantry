package com.fcastro.account.accountGroup;

import com.fcastro.app.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/accountGroups")
public class AccountGroupController {

    private static final String ROLE_ROOT = "ROLE_ROOT";
    private final AccountGroupService service;

    public AccountGroupController(AccountGroupService accountGroupService) {
        this.service = accountGroupService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AccountGroupDto> get(@PathVariable long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroup not found"));
    }

    @GetMapping
    public ResponseEntity<List<AccountGroupDto>> getAll(Authentication authentication) {
        if (isRootAccount(authentication))
            return ResponseEntity.ok(service.getAll());

        var accountId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(service.getAll(accountId));
    }

    @PostMapping
    public ResponseEntity<AccountGroupDto> create(Authentication authentication, @RequestBody AccountGroupDto newDto) {
        var accountId = Long.valueOf(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(accountId, newDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<AccountGroupDto> replace(Authentication authentication, @Valid @RequestBody AccountGroupDto newDto, @PathVariable Long id) {
        var accountId = Long.valueOf(authentication.getName());
        var dto = service.get(id)
                .map(resource -> {
                    resource.setName(newDto.getName());
                    return service.save(accountId, resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroup not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<AccountGroupDto> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private List<String> getRoles(Authentication authentication) {
        var roles = new ArrayList<String>();
        authentication.getAuthorities().forEach(grantedAuthority -> roles.add(grantedAuthority.getAuthority()));
        return roles;
    }

    private boolean isRootAccount(Authentication authentication) {
        var rootAuthority = new SimpleGrantedAuthority(ROLE_ROOT);
        return authentication.getAuthorities().contains(rootAuthority);
    }
}
