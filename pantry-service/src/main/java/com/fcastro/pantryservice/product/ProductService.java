package com.fcastro.pantryservice.product;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.Action;
import com.fcastro.app.model.ProductDto;
import com.fcastro.kafka.event.ProductEventDto;
import com.fcastro.pantryservice.event.ProductEventProducer;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import com.fcastro.pantryservice.exception.RequestParamExpectedException;
import com.fcastro.pantryservice.pantryItem.PantryItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final PantryItemRepository pantryItemRepository;
    private final ProductEventProducer productEventProducer;
    private final ModelMapper modelMapper;


    public ProductService(ProductRepository repository, PantryItemRepository pantryItemRepository, ProductEventProducer productEventProducer, ModelMapper modelMapper) {
        this.repository = repository;
        this.pantryItemRepository = pantryItemRepository;
        this.productEventProducer = productEventProducer;
        this.modelMapper = modelMapper;
    }

    public Optional<ProductDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<ProductDto> get(String code) {
        return repository.findByCode(code)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<ProductDto> getAll(String searchParam) {
        List<Product> listEntity;

        if (searchParam == null)
            throw new RequestParamExpectedException("Expecting to receive SearchParam: code or description value");

        listEntity = repository.findAllByCodeOrDescription(searchParam.toLowerCase());
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductDto> getAll() {
        List<Product> listEntity = repository.findAll(Sort.by("code"));
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDto save(ProductDto dto) {
        var entity = repository.save(convertToEntity(dto));

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.UPDATE)
                .product(ProductDto.builder()
                        .id(entity.getId())
                        .code(entity.getCode())
                        .description(entity.getDescription())
                        .size(entity.getSize())
                        .category(entity.getCategory())
                        .build())
                .build());

        return convertToDTO(entity);
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (pantryItemRepository.countPantryItem(id) > 0)
            throw new DatabaseConstraintException("Product can not be removed. It is referred in one or more pantry items.");

        repository.deleteById(id);

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.DELETE)
                .product(ProductDto.builder()
                        .id(id)
                        .build())
                .build());
    }

    private ProductDto convertToDTO(Product entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, ProductDto.class);
    }

    private Product convertToEntity(ProductDto dto) {
        if (dto == null) return null;
        var entity = modelMapper.map(dto, Product.class);
        if (entity.getCode() != null) entity.setCode(entity.getCode().toUpperCase());
        return entity;
    }
}
