package com.fcastro.pantry.pantry;

import com.fcastro.pantry.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
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
        var listEntity = repository.findAll(Sort.by("name"));
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

    private PantryDto convertToDTO(Pantry entity) {
        if (entity == null) return null;
        return PantryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .isActive(entity.getIsActive()).build();
    }

    private Pantry convertToEntity(PantryDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Pantry.class);
    }
}
