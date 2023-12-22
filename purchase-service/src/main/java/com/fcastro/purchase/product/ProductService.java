package com.fcastro.purchase.product;

import com.fcastro.kafka.event.ProductEventDto;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.model.ProductDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ModelMapper modelMapper;


    public ProductService(ProductRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public Optional<ProductDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public List<ProductDto> getAll() {
        List<Product> listEntity = repository.findAll(Sort.by("code"));
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDto save(ProductDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void processProductEvent(ProductEventDto eventDto) {
        try {
            switch (eventDto.getAction()) {
                case UPDATE -> repository.save(convertToEntity(eventDto));
                case DELETE -> repository.deleteById(eventDto.getProduct().getId());
                default -> throw new IllegalArgumentException("Action not supported: " + eventDto.getAction());
            }
        } catch (Throwable ex) {
            throw new EventProcessingException("Error occurred while processing a ProductEvent.", ex);
        }
    }

    private Product convertToEntity(ProductEventDto eventDto) {
        if (eventDto == null) return null;
        return Product.builder()
                .id(eventDto.getProduct().getId())
                .code(eventDto.getProduct().getCode())
                .description(eventDto.getProduct().getDescription())
                .size(eventDto.getProduct().getSize())
                .category(eventDto.getProduct().getCategory())
                .build();
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
