package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.security.core.model.AccountGroupMemberDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/accountservice/accountGroupMembers")
public class AccountGroupMemberController {

    private final AccountGroupMemberService service;

    public AccountGroupMemberController(AccountGroupMemberService accountGroupMemberService) {
        this.service = accountGroupMemberService;
    }

    @GetMapping()
    public ResponseEntity<List<AccountGroupMemberDto>> getAll(@RequestParam(required = false) Long groupId, @RequestParam(required = false) String email) {
        List<AccountGroupMemberDto> list;
        var groupFilter = groupId != null && groupId > 0;
        var emailFilter = email != null && email.length() > 0;

        list = groupFilter && emailFilter ? Arrays.asList(service.getByGroupIdAndEmail(groupId, email).get()) :
                groupFilter ? service.getAllByGroupId(groupId) :
                        emailFilter ? service.getAllByEmail(email) : null;
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<AccountGroupMemberDto> create(@Valid @RequestBody AccountGroupMemberDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @DeleteMapping("/{groupId}/{accountId}")
    public ResponseEntity<AccountGroupMemberDto> delete(@PathVariable Long groupId, @PathVariable Long accountId) {
        service.delete(groupId, accountId);
        return ResponseEntity.noContent().build();
    }
}
