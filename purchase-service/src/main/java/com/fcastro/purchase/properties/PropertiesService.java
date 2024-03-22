package com.fcastro.purchase.properties;

import com.fcastro.app.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropertiesService {

    private final PropertiesRepository repository;
    private final ModelMapper modelMapper;

    public PropertiesService(PropertiesRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public Optional<PropertiesDto> get(String key) {
        return repository.findById(key)
                .map(this::convertToDTO);
    }

    public List<PropertiesDto> getAll(String key) {
        List<Properties> listEntity = repository.findAllLikeId(key);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PropertiesDto> getAll() {
        List<Properties> listEntity = repository.findAll(Sort.by("propertiesKey"));
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PropertiesDto save(PropertiesDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }


    public void delete(String key) {
        repository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        repository.deleteById(key);
    }

    private PropertiesDto convertToDTO(Properties entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PropertiesDto.class);
    }

    private Properties convertToEntity(PropertiesDto dto) {
        if (dto == null) return null;
        var entity = modelMapper.map(dto, Properties.class);
        if (entity.getPropertyKey() != null) entity.setPropertyKey(entity.getPropertyKey().toLowerCase());
        return entity;
    }
}
