package com.fcastro.purchaseservice.product;

import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.kafka.model.AccountEventDto;
import com.fcastro.kafka.model.ProductEventDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                case UPDATE, CREATE -> repository.save(convertToEntity(eventDto));
                case DELETE -> repository.deleteById(eventDto.getId());
                default -> throw new IllegalArgumentException("Action not supported: " + eventDto.getAction());
            }
        } catch (Throwable ex) {
            throw new EventProcessingException("Error occurred while processing a ProductEvent.", ex);
        }
    }

    //When deleting an Account all products associated to the Account will be deleted
    @Transactional(rollbackFor = Exception.class) //Rollback will also occur for checked exceptions
    public void delete(AccountEventDto eventDto) {

        repository.deleteAllById(eventDto.getProductIds());
    }

    private Product convertToEntity(ProductEventDto eventDto) {
        if (eventDto == null) return null;
        return Product.builder()
                .id(eventDto.getId())
                .code(eventDto.getCode())
                .description(eventDto.getDescription())
                .size(eventDto.getSize())
                .category(eventDto.getCategory())
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
