package com.fcastro.pantryservice.pantry;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.accesscontrol.AccessControlService;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.core.model.AccountGroupDto;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PantryService {

    private final PantryRepository repository;
    private final ModelMapper modelMapper;
    private final AuthorizationHandler authorizationService;
    private final AccessControlService accessControlService;

    public PantryService(PantryRepository repository, ModelMapper modelMapper, AuthorizationHandler authorizationService, AccessControlService accessControlService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.authorizationService = authorizationService;
        this.accessControlService = accessControlService;
    }

    public Optional<PantryDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<PantryDto> get(String name) {
        return repository.findByName(name)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<PantryDto> getAll() {
        var accountGroups = authorizationService.getAccountGroupList(SecurityContextHolder.getContext().getAuthentication().getName());
        var listEntity = repository.findAllByAccountGroup(accountGroups);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PantryDto> getAll(Long groupId) {
        var listEntity = repository.findAllByAccountGroup(Set.of(groupId));
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<AccountGroupDto> getAccessControl(Long pantryId) {
        var access = accessControlService.get(Pantry.class.getSimpleName(), pantryId);
        return Optional.of(AccountGroupDto.builder().id(access.getAccountGroupId()).build());
    }

    public PantryDto save(PantryDto dto) {
        if (dto.getAccountGroupId() == null)
            throw new AccessControlNotDefinedException("Pantry must be associated to a group");

        var entity = repository.save(convertToEntity(dto));
        accessControlService.save(Pantry.class.getSimpleName(), entity.getId(), dto.getAccountGroupId());

        var storedDto = convertToDTO(entity);
        storedDto.setAccountGroupId(dto.getAccountGroupId());

        return storedDto;
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pantry not found"));

        repository.deleteById(id);

        accessControlService.delete(Pantry.class.getSimpleName(), id);
    }

    private PantryDto convertToDTO(Pantry entity) {
        if (entity == null) return null;
        return PantryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .isActive(entity.getIsActive())
                .accountGroupId(entity.getAccountGroupId())
                .build();
    }

    private Pantry convertToEntity(PantryDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Pantry.class);
    }

}
