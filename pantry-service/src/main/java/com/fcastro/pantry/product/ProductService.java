package com.fcastro.pantry.product;

import com.fcastro.pantry.exception.DatabaseConstraintException;
import com.fcastro.pantry.exception.ResourceNotFoundException;
import com.fcastro.pantry.pantryItem.PantryItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final PantryItemRepository pantryItemRepository;
    private final ModelMapper modelMapper;


    public ProductService(ProductRepository repository, PantryItemRepository pantryItemRepository, ModelMapper modelMapper) {
        this.repository = repository;
        this.pantryItemRepository = pantryItemRepository;
        this.modelMapper = modelMapper;
    }

    public Optional<ProductDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<ProductDto> getAll(String code, String description) {
        List<Product> listEntity;

        if (code != null && description == null){
            listEntity = repository.findAllByCode(code.toLowerCase());
        }
        else if (description != null && code == null){
            listEntity = repository.findAllByDescription(description.toLowerCase());
        }
        else listEntity = repository.findAll(Sort.by("code"));
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDto save(ProductDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (pantryItemRepository.countPantryItem(id) > 0)
            throw new DatabaseConstraintException("Product can not be removed. It is referred in one or more pantry items.");

        repository.deleteById(id);
    }

    private ProductDto convertToDTO(Product entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, ProductDto.class);
    }

    private Product convertToEntity(ProductDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Product.class);
    }
}
