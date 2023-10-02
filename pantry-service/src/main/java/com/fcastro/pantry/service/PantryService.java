package com.fcastro.pantry.service;

import com.fcastro.pantry.config.ResourceNotFoundException;
import com.fcastro.pantry.model.PantryDto;
import com.fcastro.pantry.model.PantryEntity;
import com.fcastro.pantry.repository.PantryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PantryService {

    private final PantryRepository repository;

    private final ModelMapper modelMapper;

    public PantryService(PantryRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public Optional<PantryDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<PantryDto> getAll() {
        var listEntity = repository.findAll();
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PantryDto save(PantryDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pantry not found"));

        repository.deleteById(id);
    }

    private PantryDto convertToDTO(PantryEntity entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PantryDto.class);
    }

    private PantryEntity convertToEntity(PantryDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, PantryEntity.class);
    }
}
