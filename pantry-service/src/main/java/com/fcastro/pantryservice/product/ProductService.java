package com.fcastro.pantryservice.product;

import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.Action;
import com.fcastro.kafka.event.ProductEventDto;
import com.fcastro.pantryservice.event.ProductEventProducer;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import com.fcastro.pantryservice.exception.RequestParamExpectedException;
import com.fcastro.pantryservice.pantryitem.PantryItemRepository;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final PantryItemRepository pantryItemRepository;
    private final ProductEventProducer productEventProducer;
    private final ModelMapper modelMapper;
    private final AuthorizationHandler authorizationHandler;

    public ProductService(ProductRepository repository, PantryItemRepository pantryItemRepository, ProductEventProducer productEventProducer, ModelMapper modelMapper, AuthorizationHandler authorizationHandler) {
        this.repository = repository;
        this.pantryItemRepository = pantryItemRepository;
        this.productEventProducer = productEventProducer;
        this.modelMapper = modelMapper;
        this.authorizationHandler = authorizationHandler;
    }

    public Optional<ProductDto> getEmbeddingAccountGroup(String email, long id) {
        var accessControlList = authorizationHandler.listAccessControl(email, Product.class.getSimpleName(), id, null, null);
        var product = repository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.product.not.found")));
        return Optional.of(embedAccountGroup(product, accessControlList));
    }


    public Optional<ProductDto> get(Long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<ProductDto> get(String code) {
        return repository.findByCode(code)
                .map(this::convertToDTO);
    }

    //TODO: Pageable
    public List<ProductDto> getAllBySearchParam(String email, Long groupId, String searchParam) {
        if (groupId == null)
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.param.groupId.required"));

        if (searchParam == null)
            throw new RequestParamExpectedException(MessageTranslator.getMessage("error.param.code.or.description.required"));

        var accessControlList = authorizationHandler.listAccessControl(email, Product.class.getSimpleName(), null, groupId, null);
        var productIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(toSet());

        var productList = repository.findAllByCodeOrDescription(searchParam.toLowerCase(), productIds).stream()
                .map(this::convertToDTO).collect(Collectors.toList());

        productList = embedAccountGroup(productList, accessControlList);
        return productList;
    }

    public List<ProductDto> getAll(String email) {

        var accessControlList = authorizationHandler.listAccessControl(email, Product.class.getSimpleName(), null, null, null);

        var productIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(toSet());
        var productList = repository.findAllByIds(productIds).stream().map(this::convertToDTO).collect(Collectors.toList());
        productList = embedAccountGroup(productList, accessControlList);
        return productList;
    }

    //It finds and attaches AccountGroup to each Product in the list
    private List<ProductDto> embedAccountGroup(List<ProductDto> productList, List<AccessControlDto> accessControlList) {
        return productList.stream().map((product) -> embedAccountGroup(product, accessControlList)).collect(Collectors.toList());
    }

    //It finds and attaches AccountGroup to the Product
    private ProductDto embedAccountGroup(ProductDto product, List<AccessControlDto> accessControlList) {
        accessControlList.stream()
                .filter((accessControl) -> Objects.equals(accessControl.getClazzId(), product.getId()))
                .findFirst()
                .ifPresentOrElse(
                        accessControl -> product.setAccountGroup(accessControl.getAccountGroup()),
                        () -> {
                            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.embedding.group.to.product"));
                        }
                );
        return product;
    }

    public ProductDto create(ProductDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.product.and.group.association.required"));

        existsInAccountGroup(dto);

        var entity = repository.save(convertToEntity(dto));
        authorizationHandler.saveAccessControl(Product.class.getSimpleName(), entity.getId(), dto.getAccountGroup().getId());

        var storedDto = convertToDTO(entity);
        storedDto.setAccountGroup(dto.getAccountGroup());

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.CREATE)
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .size(entity.getSize())
                .category(entity.getCategory())
                .build());

        return storedDto;
    }

    //TODO: does it scale?
    private void existsInAccountGroup(ProductDto dto) {
        var productList = repository.findAllByCode(dto.getCode()); //This can be a huge list
        var accessList = authorizationHandler.listAccessControl(SecurityContextHolder.getContext().getAuthentication().getName(), "Product", null, dto.getAccountGroup().getId(), null);

        var found = accessList.stream()
                .map(AccessControlDto::getClazzId)
                .anyMatch(productList.stream()
                        .map(Product::getId)
                        .collect(toSet())::contains);

        if (found)
            throw new DatabaseConstraintException(MessageTranslator.getMessage("error.product.already.exists.in.group"));
    }

    public ProductDto update(ProductDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.product.and.group.association.required"));

        var entity = repository.save(convertToEntity(dto));
        authorizationHandler.saveAccessControl(Product.class.getSimpleName(), entity.getId(), dto.getAccountGroup().getId());

        var storedDto = convertToDTO(entity);
        storedDto.setAccountGroup(dto.getAccountGroup());

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.UPDATE)
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .size(entity.getSize())
                .category(entity.getCategory())
                .build());

        return storedDto;
    }

    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.product.not.found")));

        if (pantryItemRepository.countPantryItem(id) > 0)
            throw new DatabaseConstraintException(MessageTranslator.getMessage("error.delete.product.in.use"));

        repository.deleteById(id);
        authorizationHandler.deleteAccessControl(Product.class.getSimpleName(), id);

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.DELETE)
                .id(id)
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
