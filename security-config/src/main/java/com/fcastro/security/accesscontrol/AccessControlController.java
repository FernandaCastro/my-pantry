package com.fcastro.security.accesscontrol;

import com.fcastro.security.core.model.AccessControlDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        var list = accessControlService.get(accountGroupId);
        return ResponseEntity.ok(list);
    }
}
