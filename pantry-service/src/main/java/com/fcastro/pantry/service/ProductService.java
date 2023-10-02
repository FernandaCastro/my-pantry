package com.fcastro.pantry.service;

import com.fcastro.pantry.config.ResourceNotFoundException;
import com.fcastro.pantry.model.ProductDto;
import com.fcastro.pantry.model.ProductEntity;
import com.fcastro.pantry.repository.ProductRepository;
import org.modelmapper.ModelMapper;
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

    //TODO: Pageable
    public List<ProductDto> getAll() {
        var listEntity = repository.findAll();
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDto save(ProductDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        repository.deleteById(id);
    }

    private ProductDto convertToDTO(ProductEntity entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, ProductDto.class);
    }

    private ProductEntity convertToEntity(ProductDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, ProductEntity.class);
    }
}
