package com.fcastro.pantry.service;

import com.fcastro.pantry.config.ResourceNotFoundException;
import com.fcastro.pantry.model.PantryProductDto;
import com.fcastro.pantry.model.PantryProductEntity;
import com.fcastro.pantry.model.PantryProductPk;
import com.fcastro.pantry.repository.PantryProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PantryProductService {

    private final PantryProductRepository repository;

    private final ModelMapper modelMapper;

    public PantryProductService(PantryProductRepository pantryProductRepository, ModelMapper modelMapper) {
        this.repository = pantryProductRepository;
        this.modelMapper = modelMapper;
    }

    public Optional<PantryProductDto> get(long pantryId, long productId) {
        return repository.findById(new PantryProductPk(pantryId, productId))
                .map(this::convertToDTO);
    }

    public List<PantryProductDto> getAll(long pantryId) {
        var listEntity = repository.findAllByPantryId(pantryId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PantryProductDto save(PantryProductDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long pantryId, long productId) {
        var pk = new PantryProductPk(pantryId, productId);
        repository.findById(pk)
                .orElseThrow(() -> new ResourceNotFoundException("Pantry Product not found"));

        repository.deleteById(pk);
    }

    private PantryProductDto convertToDTO(PantryProductEntity entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PantryProductDto.class);
    }

    private PantryProductEntity convertToEntity(PantryProductDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, PantryProductEntity.class);
    }

}
