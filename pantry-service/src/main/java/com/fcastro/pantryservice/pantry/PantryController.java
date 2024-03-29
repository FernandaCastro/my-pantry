package com.fcastro.pantryservice.pantry;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.AccountGroupDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pantries")
public class PantryController {

    private final PantryService service;

    public PantryController(PantryService service) {
        this.service = service;
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission('Pantry', #pantryId, 'list_pantry')")
    public ResponseEntity<PantryDto> get(@P("pantryId") @PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Pantry not found"));
    }

    @GetMapping(path = "/{id}/access-control")
    public ResponseEntity<AccountGroupDto> getAccessControl(@PathVariable Long id) {
        return service.getAccessControl(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("There is no Account Group associated to this Pantry."));
    }

    @GetMapping
    @PreAuthorize("hasPermission(#groupId, 'list_pantry')")
    public ResponseEntity<List<PantryDto>> getAll(@P("groupId") @RequestParam(required = false) Long groupId) {
        return groupId == null ?
                ResponseEntity.ok(service.getAll()) :
                ResponseEntity.ok(service.getAll(groupId));
    }

    @PostMapping
    @PreAuthorize("hasPermission(#pantry.getAccountGroupId(), 'create_pantry')")
    public ResponseEntity<PantryDto> create(@P("pantry") @Valid @RequestBody PantryDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#pantry.getAccountGroupId(), 'edit_pantry')")
    ResponseEntity<PantryDto> replace(@P("pantry") @Valid @RequestBody PantryDto newDto, @PathVariable Long id) {

        var dto = service.get(id)
                .map(resource -> {
                    resource.setName(newDto.getName());
                    resource.setType(newDto.getType());
                    resource.setIsActive(newDto.getIsActive());
                    resource.setAccountGroupId(newDto.getAccountGroupId());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Pantry not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasPermission('Pantry', #pantryId, 'delete_pantry')")
    public ResponseEntity<PantryDto> delete(@P("pantryId") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
