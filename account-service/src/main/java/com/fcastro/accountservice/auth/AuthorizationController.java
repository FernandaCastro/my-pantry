package com.fcastro.accountservice.auth;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.security.core.model.AccessControlDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountservice/authorization")
public class AuthorizationController {

    private final AccessControlService accessControlService;
    private final AccountGroupMemberService accountGroupMemberService;
    private final AuthorizationService authorizationService;

    public AuthorizationController(AccessControlService accessControlService, AccountGroupMemberService accountGroupMemberService, AuthorizationService authorizationService) {
        this.accessControlService = accessControlService;
        this.accountGroupMemberService = accountGroupMemberService;
        this.authorizationService = authorizationService;
    }

    /**
     * PERMISSION
     **/

    @GetMapping("/permission-in-any-group")
    ResponseEntity<Boolean> hasPermissionInAnyGroup(@RequestParam String email, @RequestParam String permission) {
        return ResponseEntity.ok(authorizationService.hasPermissionInAnyGroup(email, permission));
    }

    @GetMapping("/permission-in-group")
    ResponseEntity<Boolean> hasPermissionInAGroup(@RequestParam String email, @RequestParam String permission, @RequestParam Long accountGroupId) {
        return ResponseEntity.ok(authorizationService.hasPermissionInGroup(email, permission, accountGroupId));
    }

    @GetMapping("/permission-in-object")
    ResponseEntity<Boolean> hasPermissionInObject(@RequestParam String email, @RequestParam String permission, @RequestParam String clazz, @RequestParam Long clazzId) {
        return ResponseEntity.ok(authorizationService.hasPermissionInObject(email, permission, clazz, clazzId));
    }

    @GetMapping("/permission-in-object-list")
    ResponseEntity<Boolean> hasPermissionInObjectList(@RequestParam String email, @RequestParam String permission, @RequestParam String clazz, @RequestParam List<Long> clazzIds) {
        return ResponseEntity.ok(authorizationService.hasPermissionInObjectList(email, permission, clazz, clazzIds));
    }


    /**
     * ACCESS CONTRTOL
     **/

    @GetMapping("/access-control/non-hierarchical")
    public ResponseEntity<List<AccessControlDto>> getNonHierarchical(@RequestParam String email, @RequestParam String clazz, @RequestParam Long accountGroupId) {
        return ResponseEntity.ok(accessControlService.getNonHierarchical(email, clazz, accountGroupId));
    }

    @GetMapping("/access-control/hierarchical")
    public ResponseEntity<List<AccessControlDto>> getHierarchical(@RequestParam String email, @RequestParam String clazz, @RequestParam(required = false) Long clazzId, @RequestParam(required = false) Long accountGroupId, @RequestParam(required = false) String permission) {
        return ResponseEntity.ok(accessControlService.getHierarchical(email, clazz, clazzId, accountGroupId, permission));
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
