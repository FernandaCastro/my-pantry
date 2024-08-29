package com.fcastro.pantryservice.pantry;

import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.pantryservice.pantryitem.PantryItem;
import com.fcastro.pantryservice.pantryitem.PantryItemService;
import com.fcastro.security.authorization.AuthorizationClient;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupDto;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PantryService {

    private final PantryRepository repository;
    private final ModelMapper modelMapper;
    private final AuthorizationClient authorizationClient;
    private final PantryItemService pantryItemService;

    public PantryService(PantryRepository repository, ModelMapper modelMapper, AuthorizationClient authorizationService, PantryItemService pantryItemService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.authorizationClient = authorizationService;
        this.pantryItemService = pantryItemService;
    }

    public Optional<PantryDto> getEmbeddingAccountGroup(String email, long id) {
        var accessControlList = authorizationClient.listAccessControl(email, Pantry.class.getSimpleName(), id, null, null);
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
        var accessControlList = authorizationClient.listAccessControl(email, Pantry.class.getSimpleName(), null, null, null);
        return getAll(accessControlList);
    }

    public List<PantryDto> getAllWithPermission(String email, String permission) {
        var accessControlList = authorizationClient.listAccessControl(email, Pantry.class.getSimpleName(), null, null, permission);
        return getAll(accessControlList);
    }

    //TODO: Pageable
    //Retrieves all pantries in the group
    public List<PantryDto> getAll(String email, Long accountGroupId) {
        var accessControlList = authorizationClient.listAccessControl(email, Pantry.class.getSimpleName(), null, accountGroupId, null);
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

        var pantryExists = dto.getId() > 0 ? repository.findById(dto.getId()) : Optional.empty();
        var entity = repository.save(convertToEntity(dto));

        //Add to access control only when adding a new Pantry
        if (!pantryExists.isPresent()) {
            authorizationClient.saveAccessControl(Pantry.class.getSimpleName(), entity.getId(), dto.getAccountGroup().getId());
        }

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

        authorizationClient.deleteAccessControl(Pantry.class.getSimpleName(), id);
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
    public PantryDto createWizard(PantryWizardDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.pantry.and.group.association.required"));

        var createdPantry = createPantyAndItems(dto);

        if (dto.isAnalysePantry()) {
            var items = pantryItemService.processPurchaseNeed(createdPantry.getId());
            createdPantry.setItems(items);
        }

        return createdPantry;
    }

    @Transactional
    public PantryDto createPantyAndItems(PantryWizardDto dto) {
        var pantryDto = PantryDto.builder()
                .name(dto.getName())
                .type(dto.getType())
                .isActive(true)
                .accountGroup(dto.getAccountGroup())
                .build();
        var pantryEntity = repository.save(convertToEntity(pantryDto));
        authorizationClient.saveAccessControl(Pantry.class.getSimpleName(), pantryEntity.getId(), dto.getAccountGroup().getId());

        var storedDto = convertToDTO(pantryEntity);
        storedDto.setAccountGroup(dto.getAccountGroup());

        var items = pantryItemService.createWizard(storedDto, dto.getItems());
        storedDto.setItems(items);

        return storedDto;
    }


    /**
     * Calculate the consume percentage of all pantries the user has access,
     * and list the 5 items with the lowest consume percentage in each pantry
     *
     * @param email String
     * @return List<PantryChartDto>
     */
    @Transactional
    public List<PantryChartDto> getChartData(String email) {
        var accessControlList = authorizationClient.listAccessControl(email, Pantry.class.getSimpleName(), null, null, null);
        var pantryIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(Collectors.toSet());
        var pantryList = repository.findAllByIds(pantryIds);

        var pantryChartList = pantryList.stream().map(p -> {
            var items = p.getItems();
            var pantryChart = PantryChartDto.builder()
                    .id(p.getId()).name(p.getName()).type(p.getType()).isActive(p.getIsActive())
                    .percentage(calculatePercentage(items))
                    .criticalItems(calculateCriticalItems(items))
                    .accountGroup(getAccountGroup(p.getId(), accessControlList))
                    .build();

            return pantryChart;
        }).collect(Collectors.toList());

        return pantryChartList;
    }

    //It finds and attaches AccountGroup to the Pantry
    private AccountGroupDto getAccountGroup(Long pantryId, List<AccessControlDto> accessControlList) {
        var accountControl = accessControlList.stream()
                .filter((accessControl) -> accessControl.getClazzId() == pantryId)
                .findFirst()
                .orElseThrow(() -> new AccessControlNotDefinedException(MessageTranslator.getMessage("error.embedding.group.to.pantry")));

        return accountControl.getAccountGroup();
    }

    private double calculatePercentage(List<PantryItem> items) {
        var percentual = 0.0;

        var filteredList = items.stream().filter(i -> i.getIdealQty() > 0).collect(Collectors.toList());

        //Math.min(value, 100): Limit the availability percentual to 100% to prevent excessive stocks from distorting the calculation.
        var sum = filteredList.stream()
                .map(i -> Math.min((Double.valueOf(i.getCurrentQty()) / Double.valueOf(i.getIdealQty())) * 100, 100))
                .reduce(0.0, Double::sum);

        percentual = sum / items.size();

        // Define the scale Factor (10^decimals)
        double scaleFactor = Math.pow(10, 2);

        // Apply Math.ceil and limit decimals
        percentual = Math.ceil(sum / items.size() * scaleFactor) / scaleFactor;

        return percentual;
    }

    private List<PantryItemChartDto> calculateCriticalItems(List<PantryItem> items) {

        var criticalItems = items.stream()
                .filter(i -> i.getIdealQty() > 0)
                .map(i -> {
                    var percentage = 0.0;

                    if (i.getIdealQty() > 0) {
                        percentage = (100 * i.getCurrentQty()) / i.getIdealQty();
                    }

                    return PantryItemChartDto.builder()
                            .productId(i.getProduct().getId())
                            .productCode(i.getProduct().getCode())
                            .idealQty(i.getIdealQty())
                            .currentQty(i.getCurrentQty())
                            .percentage(percentage)
                            .build();

                })
                .filter(i -> i.getPercentage() <= 30)
                .sorted(Comparator.comparingDouble(PantryItemChartDto::getPercentage))
                .collect(Collectors.toList());

        if (criticalItems.size() > 5) {
            return criticalItems.subList(0, 5);
        }
        return criticalItems;
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
