package com.fcastro.pantry.controller;

import com.fcastro.pantry.config.ResourceNotFoundException;
import com.fcastro.pantry.model.PantryProductDto;
import com.fcastro.pantry.service.PantryProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pantry/{pantryId}/product")
public class PantryProductController {

    private final PantryProductService service;

    public PantryProductController(PantryProductService service) {
        this.service = service;
    }

    @GetMapping(path = "/{productId}")
    public ResponseEntity<PantryProductDto> get(@PathVariable long pantryId, @PathVariable long productId) {
        return service.get(pantryId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PantryProductDto>> getAll(@PathVariable long pantryId) {
        return ResponseEntity.ok(service.getAll(pantryId));
    }

    @PostMapping
    public ResponseEntity<PantryProductDto> create(@RequestBody PantryProductDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{productId}")
    ResponseEntity<PantryProductDto> replace(@RequestBody PantryProductDto newDto, @PathVariable long pantryId, @PathVariable long productId) {
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

    @DeleteMapping("/{productId}")
    public ResponseEntity<PantryProductDto> delete(@PathVariable long pantryId, @PathVariable long productId) {
        service.delete(pantryId, productId);
        return ResponseEntity.noContent().build();
    }
}
