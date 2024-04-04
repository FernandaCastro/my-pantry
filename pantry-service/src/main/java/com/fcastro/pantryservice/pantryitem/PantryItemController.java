package com.fcastro.pantryservice.pantryitem;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pantries/{pantryId}")
public class PantryItemController {

    private final PantryItemService service;

    public PantryItemController(PantryItemService service) {
        this.service = service;
    }

    @GetMapping(path = "/items/{productId}")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'list_pantry_item')")
    public ResponseEntity<PantryItemDto> get(@P("pantryId") @PathVariable long pantryId, @PathVariable long productId) {
        return service.get(pantryId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/items")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'list_pantry_item')")
    public ResponseEntity<List<PantryItemDto>> getAll(@P("pantryId") @PathVariable long pantryId) {
        return ResponseEntity.ok(service.getAll(pantryId));
    }

    @GetMapping("/items/consume")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'consume_pantry')")
    public ResponseEntity<List<PantryItemDto>> getAllConsume(@P("pantryId") @PathVariable long pantryId) {
        return ResponseEntity.ok(service.getAllConsume(pantryId));
    }

    @PostMapping("/items")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'add_pantry_item')")
    public ResponseEntity<PantryItemDto> create(@P("pantryId") @PathVariable long pantryId, @RequestBody PantryItemDto newDto) {
        if (service.get(newDto.getPantryId(), newDto.getProductId()).isPresent())
            throw new DatabaseConstraintException("This item is already in the pantry. It shouldn't be added again.");

        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/items/{productId}")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'edit_pantry_item')")
    ResponseEntity<PantryItemDto> replace(@RequestBody PantryItemDto newDto, @P("pantryId") @PathVariable long pantryId, @PathVariable long productId) {
        var dto = service.get(pantryId, productId)
                .map(resource -> {
                    resource.setIdealQty(newDto.getIdealQty());
                    resource.setCurrentQty(newDto.getCurrentQty());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Pantry Item not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'delete_pantry_item')")
    public ResponseEntity<PantryItemDto> delete(@P("pantryId") @PathVariable long pantryId, @PathVariable long productId) {
        service.delete(pantryId, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("items/consume")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'consume_pantry')")
    public ResponseEntity<List<PantryItemDto>> consumeProduct(@P("pantryId") @PathVariable Long pantryId, @RequestBody List<PantryItemConsumedDto> items) {
        service.consumePantryItem(pantryId, items);
        var list = service.getAll(pantryId);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("items/balancing")
    @PreAuthorize("hasPermissionInObject('Pantry', #pantryId, 'analyse_pantry')")
    public ResponseEntity<List<PantryItemDto>> balanceInventory(@P("pantryId") @PathVariable Long pantryId) {
        var list = service.processPurchaseNeed(pantryId);
        return ResponseEntity.ok().body(list);
    }
}
