package com.fcastro.account.accountGroupMember;

import com.fcastro.app.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountGroups/{groupId}/members")
public class AccountGroupMemberController {

    private final AccountGroupMemberService service;

    public AccountGroupMemberController(AccountGroupMemberService accountGroupMemberService) {
        this.service = accountGroupMemberService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AccountGroupMemberDto> get(@PathVariable long groupId, @PathVariable long id) {
        return service.get(groupId, id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("AccountGroupMember not found"));
    }

    @GetMapping
    public ResponseEntity<List<AccountGroupMemberDto>> getAll(Authentication authentication, @PathVariable long groupId) {
        var accountId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(service.getAll(accountId, groupId));
    }

    @PostMapping
    public ResponseEntity<AccountGroupMemberDto> create(@Valid @RequestBody AccountGroupMemberDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<AccountGroupMemberDto> delete(@PathVariable Long groupId, @PathVariable Long id) {
        service.delete(groupId, id);
        return ResponseEntity.noContent().build();
    }
}
