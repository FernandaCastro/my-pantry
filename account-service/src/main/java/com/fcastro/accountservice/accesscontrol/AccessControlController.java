package com.fcastro.accountservice.accesscontrol;

import com.fcastro.security.core.model.AccessControlDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accessControl")
public class AccessControlController {

    private final AccessControlService accessControlService;

    public AccessControlController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public ResponseEntity<List<AccessControlDto>> getAll(@RequestParam Long accountGroupId) {
        var list = accessControlService.getAll(accountGroupId);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public ResponseEntity<AccessControlDto> get(@RequestParam String clazz, @RequestParam Long clazzId) {
        return ResponseEntity.ok(accessControlService.get(clazz, clazzId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public ResponseEntity<AccessControlDto> save(@RequestBody AccessControlDto accessControlDto) {
        accessControlService.save(accessControlDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public ResponseEntity<AccessControlDto> delete(@RequestParam String clazz, @RequestParam Long clazzId) {
        accessControlService.delete(clazz, clazzId);
        return ResponseEntity.noContent().build();
    }
}
