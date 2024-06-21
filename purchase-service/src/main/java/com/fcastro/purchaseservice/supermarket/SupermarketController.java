package com.fcastro.purchaseservice.supermarket;

import com.fcastro.app.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchaseservice/supermarkets")
public class SupermarketController {

    private final SupermarketService service;

    public SupermarketController(SupermarketService service) {
        this.service = service;
    }

    @GetMapping("/all")
    @PreAuthorize("hasPermissionInAnyGroup('list_supermarket')")
    public ResponseEntity<List<SupermarketDto>> getAll() {
        return ResponseEntity.ok(service.getAll(SecurityContextHolder.getContext().getAuthentication().getName(), null));
    }

    @GetMapping
    @PreAuthorize("hasPermissionInAGroup(#groupId, 'list_supermarket')")
    public ResponseEntity<List<SupermarketDto>> getAll(@P("groupId") @RequestParam Long groupId) {
        return ResponseEntity.ok(service.getAll(SecurityContextHolder.getContext().getAuthentication().getName(), groupId));
    }

    @PostMapping
    @PreAuthorize("hasPermissionInAGroup(#supermarket.getAccountGroup().getId(), 'create_supermarket')")
    public ResponseEntity<SupermarketDto> create(@P("supermarket") @Valid @RequestBody SupermarketDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermissionInObject('Supermarket', #supermarketId, 'edit_supermarket')")
    ResponseEntity<SupermarketDto> replace(@Valid @RequestBody SupermarketDto newDto, @P("supermarketId") @PathVariable Long id) {

        var dto = service.get(id)
                .map(resource -> {
                    resource.setName(newDto.getName());
                    resource.setCategories(newDto.getCategories());
                    resource.setAccountGroup(newDto.getAccountGroup());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Supermarket not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasPermissionInObject('Supermarket', #supermarketId, 'delete_supermarket')")
    public ResponseEntity<SupermarketDto> delete(@P("supermarketId") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
