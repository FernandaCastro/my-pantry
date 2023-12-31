package com.fcastro.purchase.properties;

import com.fcastro.purchase.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin()
@RequestMapping("properties")
public class PropertiesController {

    private final PropertiesService service;

    public PropertiesController(PropertiesService service) {
        this.service = service;
    }

    @GetMapping(path = "/{key}")
    public ResponseEntity<PropertiesDto> get(@PathVariable String key) {
        return service.get(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<PropertiesDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }


    @PostMapping
    public ResponseEntity<PropertiesDto> create(@Valid @RequestBody PropertiesDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{key}")
    ResponseEntity<PropertiesDto> replace(@Valid @RequestBody PropertiesDto newDto, @PathVariable String key) {

        var dto = service.get(key)
                .map(resource -> {
                    resource.setPropertyValue(newDto.getPropertyValue());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{key}")
    public ResponseEntity<PropertiesDto> delete(@PathVariable String key) {
        service.delete(key);
        return ResponseEntity.noContent().build();
    }
}
