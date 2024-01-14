package com.fcastro.pantry.pantryItem;

import com.fcastro.pantry.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PantryItemDto> get(@PathVariable long pantryId, @PathVariable long productId) {
        return service.get(pantryId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/items")
    public ResponseEntity<List<PantryItemDto>> getAll(@PathVariable long pantryId) {
        return ResponseEntity.ok(service.getAll(pantryId));
    }

    @GetMapping("/items/consume")
    public ResponseEntity<List<PantryItemDto>> getAllConsume(@PathVariable long pantryId) {
        return ResponseEntity.ok(service.getAllConsume(pantryId));
    }

    @PostMapping("/items")
    public ResponseEntity<PantryItemDto> create(@RequestBody PantryItemDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/items/{productId}")
    ResponseEntity<PantryItemDto> replace(@RequestBody PantryItemDto newDto, @PathVariable long pantryId, @PathVariable long productId) {
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
    public ResponseEntity<PantryItemDto> delete(@PathVariable long pantryId, @PathVariable long productId) {
        service.delete(pantryId, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("items/consume")
    public ResponseEntity<List<PantryItemDto>> consumeProduct(@PathVariable Long pantryId, @RequestBody List<PantryItemConsumedDto> items) {
        service.consumePantryItem(pantryId, items);
        var list = service.getAll(pantryId);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/balancing")
    public ResponseEntity<List<PantryItemDto>> consumeProduct(@PathVariable Long pantryId) {
        var list = service.processPurchaseNeed(pantryId);
        return ResponseEntity.ok().body(list);
    }
}
