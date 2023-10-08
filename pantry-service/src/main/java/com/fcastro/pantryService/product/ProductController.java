package com.fcastro.pantryService.product;

import com.fcastro.pantryService.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<ProductDto> replace(@RequestBody ProductDto newDto, @PathVariable Long id) {

        var dto = service.get(id)
                .map(resource -> {
                    resource.setCode(newDto.getCode());
                    resource.setDescription(newDto.getDescription());
                    resource.setSize(newDto.getSize());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ProductDto> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
