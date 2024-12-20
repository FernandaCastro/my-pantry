package com.fcastro.pantryservice.pantry;

import com.fcastro.commons.config.MessageTranslator;
import com.fcastro.commons.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pantryservice/pantries")
public class PantryController {

    private final PantryService service;

    public PantryController(PantryService service) {
        this.service = service;
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'list_pantry')")
    public ResponseEntity<PantryDto> get(@P("pantryId") @PathVariable Long id) {
        return service.getEmbeddingAccountGroup(SecurityContextHolder.getContext().getAuthentication().getName(), id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.pantry.not.found")));
    }

    @GetMapping("/all")
    @PreAuthorize("hasPermissionInAnyGroup('list_pantry')")
    public ResponseEntity<List<PantryDto>> getAll() {
        return ResponseEntity.ok(service.getAll(SecurityContextHolder.getContext().getAuthentication().getName()));
    }


    @GetMapping("/all-with-permission")
    public ResponseEntity<List<PantryDto>> getAllWithPermission(@RequestParam String permission) {
        return ResponseEntity.ok(service.getAllWithPermission(SecurityContextHolder.getContext().getAuthentication().getName(), permission));
    }

    @GetMapping
    @PreAuthorize("hasPermissionInAGroup(#groupId, 'list_pantry')")
    public ResponseEntity<List<PantryDto>> getAll(@P("groupId") @RequestParam Long groupId, @RequestParam(defaultValue = "true") boolean hierarchical) {
        return hierarchical ?
                ResponseEntity.ok(service.getAll(SecurityContextHolder.getContext().getAuthentication().getName(), groupId)) :
                ResponseEntity.ok(service.getAllNonHierarchical(SecurityContextHolder.getContext().getAuthentication().getName(), groupId));
    }

    @PostMapping
    @PreAuthorize("hasPermissionInAGroup(#pantry.getAccountGroup().getId(), 'create_pantry')")
    public ResponseEntity<PantryDto> create(@P("pantry") @Valid @RequestBody PantryDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'edit_pantry')")
    ResponseEntity<PantryDto> replace(@Valid @RequestBody PantryDto newDto, @P("pantryId") @PathVariable Long id) {

        var dto = service.get(id)
                .map(resource -> {
                    resource.setName(newDto.getName());
                    resource.setType(newDto.getType());
                    resource.setIsActive(newDto.getIsActive());
                    resource.setAccountGroup(newDto.getAccountGroup());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.pantry.not.found")));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'delete_pantry')")
    public ResponseEntity<PantryDto> delete(@P("pantryId") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/wizard")
    @PreAuthorize("hasPermissionInAGroup(#pantry.getAccountGroup().getId(), 'create_pantry')")
    public ResponseEntity<PantryDto> create(@P("pantry") @Valid @RequestBody PantryWizardDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createWizard(newDto));
    }

    @GetMapping("/charts-data")
    @PreAuthorize("hasPermissionInAnyGroup('list_pantry')")
    public ResponseEntity<List<PantryChartDto>> getChartData() {
        return ResponseEntity.ok(service.getChartData(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

}
