package com.fcastro.pantryInventory.pantryItem;

import com.fcastro.pantryInventory.event.PurchaseItemDto;
import com.fcastro.pantryInventory.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pantry/{pantryId}")
public class PantryItemController {

    private final PantryItemService service;

    public PantryItemController(PantryItemService service) {
        this.service = service;
    }

    @GetMapping(path = "/product/{productId}")
    public ResponseEntity<PantryItemDto> get(@PathVariable long pantryId, @PathVariable long productId) {
        return service.get(pantryId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product")
    public ResponseEntity<List<PantryItemDto>> getAll(@PathVariable long pantryId) {
        return ResponseEntity.ok(service.getAll(pantryId));
    }

    @PostMapping("/product")
    public ResponseEntity<PantryItemDto> create(@RequestBody PantryItemDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/product/{productId}")
    ResponseEntity<PantryItemDto> replace(@RequestBody PantryItemDto newDto, @PathVariable long pantryId, @PathVariable long productId) {
        var dto = service.get(pantryId, productId)
                .map(resource -> {
                    resource.setIdealQty(newDto.getIdealQty());
                    resource.setCurrentQty(newDto.getCurrentQty());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Pantry Product not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<PantryItemDto> delete(@PathVariable long pantryId, @PathVariable long productId) {
        service.delete(pantryId, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/consume-product")
    public ResponseEntity<PurchaseItemDto> consumeProduct(@PathVariable Long pantryId, @RequestBody PantryItemConsumedDto dto) {
        dto.setPantryId(pantryId);
        service.consumePantryItem(dto);
        return ResponseEntity.ok().build();
    }
}
