package com.fcastro.accountservice.accesscontrol;

import com.fcastro.security.core.model.AccessControlDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accountservice/accessControl")
public class AccessControlController {

    private final AccessControlService accessControlService;

    public AccessControlController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AccessControlDto>> getAll(@RequestParam Long accountGroupId) {
        var list = accessControlService.getAll(accountGroupId);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<AccessControlDto> get(@RequestParam String clazz, @RequestParam Long clazzId) {
        return ResponseEntity.ok(accessControlService.get(clazz, clazzId));
    }


}
