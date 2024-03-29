package com.fcastro.pantryservice.product;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.ProductDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission('Product', #productId, 'list_product')")
    public ResponseEntity<ProductDto> get(@P("productId") @PathVariable long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    @PreAuthorize("hasPermissionInAnyGroup('list_product')")
    public ResponseEntity<List<ProductDto>> getAll(@RequestParam(required = false) String searchParam) {
        if (searchParam == null)
            return ResponseEntity.ok(service.getAll(SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(service.getAllBySearchParam(searchParam));
    }

    @PostMapping
    @PreAuthorize("hasPermission(#product.getAccountGroupId(), 'create_product')")
    public ResponseEntity<ProductDto> create(@P("product") @Valid @RequestBody ProductDto newDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('Product', #productId, 'edit_product')")
    ResponseEntity<ProductDto> replace(@Valid @RequestBody ProductDto newDto, @P("productId") @PathVariable Long id) {

        var dto = service.get(id)
                .map(resource -> {
                    resource.setCode(newDto.getCode());
                    resource.setDescription(newDto.getDescription());
                    resource.setSize(newDto.getSize());
                    resource.setCategory(newDto.getCategory());
                    return service.save(resource);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasPermission('Product', #productId, 'delete_product')")
    public ResponseEntity<ProductDto> delete(@P("productId") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
