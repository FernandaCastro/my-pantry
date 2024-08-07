package com.fcastro.pantryservice.pantry;

import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.pantryservice.pantryitem.PantryItemService;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PantryService {

    private final PantryRepository repository;
    private final ModelMapper modelMapper;
    private final AuthorizationHandler authorizationHandler;
    private final PantryItemService pantryItemService;

    public PantryService(PantryRepository repository, ModelMapper modelMapper, AuthorizationHandler authorizationService, PantryItemService pantryItemService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.authorizationHandler = authorizationService;
        this.pantryItemService = pantryItemService;
    }

    public Optional<PantryDto> getEmbeddingAccountGroup(String email, long id) {
        var accessControlList = authorizationHandler.listAccessControl(email, Pantry.class.getSimpleName(), id, null, null);
        var pantry = repository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.pantry.not.found")));
        ;
        return Optional.of(embedAccountGroup(pantry, accessControlList));
    }

    public Optional<PantryDto> get(long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    public Optional<PantryDto> get(String name) {
        return repository.findByName(name)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    //Retrieves all pantries the user has access
    public List<PantryDto> getAll(String email) {
        var accessControlList = authorizationHandler.listAccessControl(email, Pantry.class.getSimpleName(), null, null, null);
        return getAll(accessControlList);
    }

    public List<PantryDto> getAllWithPermission(String email, String permission) {
        var accessControlList = authorizationHandler.listAccessControl(email, Pantry.class.getSimpleName(), null, null, permission);
        return getAll(accessControlList);
    }

    //TODO: Pageable
    //Retrieves all pantries in the group
    public List<PantryDto> getAll(String email, Long accountGroupId) {
        var accessControlList = authorizationHandler.listAccessControl(email, Pantry.class.getSimpleName(), null, accountGroupId, null);
        return getAll(accessControlList);
    }

    private List<PantryDto> getAll(List<AccessControlDto> accessControlList) {
        var pantryIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(Collectors.toSet());
        var pantryList = repository.findAllByIds(pantryIds).stream().map(this::convertToDTO).collect(Collectors.toList());
        pantryList = embedAccountGroup(pantryList, accessControlList);
        return pantryList;
    }

    //It finds and attaches AccountGroup to each Pantry in the list
    private List<PantryDto> embedAccountGroup(List<PantryDto> pantryList, List<AccessControlDto> accessControlList) {
        return pantryList.stream().map((pantry) -> embedAccountGroup(pantry, accessControlList)).collect(Collectors.toList());
    }

    //It finds and attaches AccountGroup to the Pantry
    private PantryDto embedAccountGroup(PantryDto pantry, List<AccessControlDto> accessControlList) {
        accessControlList.stream()
                .filter((accessControl) -> accessControl.getClazzId() == pantry.getId())
                .findFirst()
                .ifPresentOrElse(
                        accessControl -> pantry.setAccountGroup(accessControl.getAccountGroup()),
                        () -> {
                            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.embedding.group.to.pantry"));
                        }
                );
        return pantry;
    }

    public PantryDto save(PantryDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.pantry.and.group.association.required"));

        var entity = repository.save(convertToEntity(dto));
        authorizationHandler.saveAccessControl(Pantry.class.getSimpleName(), entity.getId(), dto.getAccountGroup().getId());

        var storedDto = convertToDTO(entity);
        storedDto.setAccountGroup(dto.getAccountGroup());

        return storedDto;
    }

    @Transactional
    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.pantry.not.found")));

        pantryItemService.deleteAllItems(id);
        repository.deleteById(id);

        authorizationHandler.deleteAccessControl(Pantry.class.getSimpleName(), id);
    }

    /**
     * Creates a new Pantry and its items. Each item will be created based on the code of a product that fits the following:
     * (1) - product associated to the informed account group, if exits
     * (2) - product associated to the parent account group, if exits
     * (3) - if product does not exit, create new product associated to the parent account group.
     *
     * @param dto
     * @return PantryDto
     */
    @Transactional
    public PantryDto createWizard(PantryWizardDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.pantry.and.group.association.required"));

        var pantryDto = PantryDto.builder()
                .name(dto.getName())
                .type(dto.getType())
                .isActive(true)
                .accountGroup(dto.getAccountGroup())
                .build();
        var pantryEntity = repository.save(convertToEntity(pantryDto));
        authorizationHandler.saveAccessControl(Pantry.class.getSimpleName(), pantryEntity.getId(), dto.getAccountGroup().getId());

        var storedDto = convertToDTO(pantryEntity);
        storedDto.setAccountGroup(dto.getAccountGroup());

        var items = pantryItemService.createWizard(storedDto, dto.getItems());
        storedDto.setItems(items);

        return storedDto;
    }

    private PantryDto convertToDTO(Pantry entity) {
        if (entity == null) return null;
        return PantryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .isActive(entity.getIsActive())
                .build();
    }

    private Pantry convertToEntity(PantryDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Pantry.class);
    }

}
