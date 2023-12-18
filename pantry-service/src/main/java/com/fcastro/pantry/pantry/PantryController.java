package com.fcastro.pantry.pantry;

import com.fcastro.pantry.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin()
@RequestMapping("pantries")
public class PantryController {

    private final PantryService service;

    public PantryController(PantryService service) {
        this.service = service;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PantryDto> get(@PathVariable long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Pantry not found"));
    }

    @GetMapping
    public ResponseEntity<List<PantryDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<PantryDto> create(@Valid @RequestBody PantryDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<PantryDto> replace(@Valid @RequestBody PantryDto newDto, @PathVariable Long id) {

        var dto = service.get(id)
                .map(resource -> {
                    resource.setName(newDto.getName());
                    resource.setType(newDto.getType());
                    resource.setIsActive(newDto.getIsActive());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Pantry not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<PantryDto> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
