package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupMemberDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountservice/authorization")
public class AuthorizationController {

    private final AccessControlService accessControlService;
    private final AccountGroupMemberService accountGroupMemberService;

    public AuthorizationController(AccessControlService accessControlService, AccountGroupMemberService accountGroupMemberService) {
        this.accessControlService = accessControlService;
        this.accountGroupMemberService = accountGroupMemberService;
    }

    @GetMapping("/permission-in-any-group")
    ResponseEntity<List<AccountGroupMemberDto>> hasPermissionInAnyGroup(@RequestParam String email, @RequestParam String permission) {
        return ResponseEntity.ok(accountGroupMemberService.hasPermissionInAnyGroup(email, permission));
    }

    @GetMapping("/permission-in-group")
    ResponseEntity<List<AccountGroupMemberDto>> hasPermissionInAGroup(@RequestParam String email, @RequestParam String permission, @RequestParam Long accountGroupId) {
        return ResponseEntity.ok(accountGroupMemberService.hasPermissionInGroup(email, permission, accountGroupId));
    }

    @GetMapping("/permission-in-object")
    ResponseEntity<AccessControlDto> hasPermissionInObject(@RequestParam String email, @RequestParam String permission, @RequestParam String clazz, @RequestParam Long clazzId) {
        var access = accessControlService.hasPermissionInObject(email, permission, clazz, clazzId);
        if (access == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(access);
    }

    @GetMapping("/permission-in-object-list")
    ResponseEntity<List<AccessControlDto>> hasPermissionInObjectList(@RequestParam String email, @RequestParam String permission, @RequestParam String clazz, @RequestParam List<Long> clazzIds) {
        return ResponseEntity.ok(accessControlService.hasPermissionInObjectList(email, permission, clazz, clazzIds));
    }

    @GetMapping("/access-control-strict")
    public ResponseEntity<List<AccessControlDto>> getAllByEmail(@RequestParam String email, @RequestParam String clazz, @RequestParam Long accountGroupId) {
        return ResponseEntity.ok(accessControlService.getAllByEmailAndAccessControlStrict(email, clazz, accountGroupId));
    }

    /**
     * Retrieves accessControl considering accountgroup hierarchy
     *
     * @param email
     * @param clazz
     * @param clazzId
     * @param accountGroupId
     * @param permission
     * @return
     */
    @GetMapping("/access-control")
    public ResponseEntity<List<AccessControlDto>> getAllByEmail(@RequestParam String email, @RequestParam String clazz, @RequestParam(required = false) Long clazzId, @RequestParam(required = false) Long accountGroupId, @RequestParam(required = false) String permission) {
        return ResponseEntity.ok(accessControlService.getAllByEmailAndAccessControl(email, clazz, clazzId, accountGroupId, permission));
    }

    @PostMapping("/access-control")
    public ResponseEntity<AccessControlDto> save(@RequestBody AccessControlDto accessControlDto) {
        accessControlService.save(accessControlDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/access-control")
    public ResponseEntity<AccessControlDto> delete(@RequestParam String clazz, @RequestParam Long clazzId) {
        accessControlService.delete(clazz, clazzId);
        return ResponseEntity.noContent().build();
    }
}
