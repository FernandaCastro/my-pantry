package com.fcastro.pantryservice.product;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.Action;
import com.fcastro.app.model.ProductDto;
import com.fcastro.kafka.event.ProductEventDto;
import com.fcastro.pantryservice.event.ProductEventProducer;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import com.fcastro.pantryservice.exception.RequestParamExpectedException;
import com.fcastro.pantryservice.pantryitem.PantryItemRepository;
import com.fcastro.security.accesscontrol.AccessControlService;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final PantryItemRepository pantryItemRepository;
    private final ProductEventProducer productEventProducer;
    private final ModelMapper modelMapper;
    private final AuthorizationHandler authorizationService;
    private final AccessControlService accessControlService;

    public ProductService(ProductRepository repository, PantryItemRepository pantryItemRepository, ProductEventProducer productEventProducer, ModelMapper modelMapper, AuthorizationHandler authorizationService, AccessControlService accessControlService) {
        this.repository = repository;
        this.pantryItemRepository = pantryItemRepository;
        this.productEventProducer = productEventProducer;
        this.modelMapper = modelMapper;
        this.authorizationService = authorizationService;
        this.accessControlService = accessControlService;
    }

    public Optional<ProductDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<ProductDto> get(String code) {
        return repository.findByCode(code)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<ProductDto> getAll(String searchParam) {
        List<Product> listEntity;

        if (searchParam == null)
            throw new RequestParamExpectedException("Expecting to receive SearchParam: code or description value");
        var accountGroups = authorizationService.getAccountGroupList(SecurityContextHolder.getContext().getAuthentication().getName());
        listEntity = repository.findAllByCodeOrDescription(searchParam.toLowerCase(), accountGroups);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductDto> getAll() {
        var accountGroups = authorizationService.getAccountGroupList(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Product> listEntity = repository.findAll(accountGroups);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDto save(ProductDto dto) {
        if (dto.getAccountGroupId() == null)
            throw new AccessControlNotDefinedException("Product must be associated to an Account Group");

        var entity = repository.save(convertToEntity(dto));
        accessControlService.save(Product.class.getSimpleName(), entity.getId(), dto.getAccountGroupId());

        var storedDto = convertToDTO(entity);
        storedDto.setAccountGroupId(dto.getAccountGroupId());

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.UPDATE)
                .product(ProductDto.builder()
                        .id(entity.getId())
                        .code(entity.getCode())
                        .description(entity.getDescription())
                        .size(entity.getSize())
                        .category(entity.getCategory())
                        .build())
                .build());

        return storedDto;
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (pantryItemRepository.countPantryItem(id) > 0)
            throw new DatabaseConstraintException("Product can not be removed. It is referred in one or more pantry items.");

        repository.deleteById(id);
        accessControlService.delete(Product.class.getSimpleName(), id);

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.DELETE)
                .product(ProductDto.builder()
                        .id(id)
                        .build())
                .build());
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
