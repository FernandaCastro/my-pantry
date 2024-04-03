package com.fcastro.pantryservice.product;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.Action;
import com.fcastro.kafka.event.ProductEventDto;
import com.fcastro.pantryservice.event.ProductEventProducer;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import com.fcastro.pantryservice.exception.RequestParamExpectedException;
import com.fcastro.pantryservice.pantry.Pantry;
import com.fcastro.pantryservice.pantryitem.PantryItemRepository;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import org.modelmapper.ModelMapper;
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
    private final AuthorizationHandler authorizationHandler;

    public ProductService(ProductRepository repository, PantryItemRepository pantryItemRepository, ProductEventProducer productEventProducer, ModelMapper modelMapper, AuthorizationHandler authorizationHandler) {
        this.repository = repository;
        this.pantryItemRepository = pantryItemRepository;
        this.productEventProducer = productEventProducer;
        this.modelMapper = modelMapper;
        this.authorizationHandler = authorizationHandler;
    }

    public Optional<ProductDto> getEmbeddingAccountGroup(String email, long id) {
        var accessControlList = authorizationHandler.listAccessControl(email, Product.class.getSimpleName(), id, null);
        var product = repository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
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
            throw new RequestParamExpectedException("Expecting to receive parameter groupId");

        if (searchParam == null)
            throw new RequestParamExpectedException("Expecting to receive SearchParam: code or description value");

        var accessControlList = authorizationHandler.listAccessControl(email, Product.class.getSimpleName(), null, groupId);
        var productIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(Collectors.toSet());

        var productList = repository.findAllByCodeOrDescription(searchParam.toLowerCase(), productIds).stream()
                .map(this::convertToDTO).collect(Collectors.toList());

        productList = embedAccountGroup(productList, accessControlList);
        return productList;
    }

    public List<ProductDto> getAll(String email) {
        var accessControl = AccessControlDto.builder()
                .clazz(Product.class.getSimpleName()).build();
        var accessControlList = authorizationHandler.listAccessControl(email, Pantry.class.getSimpleName(), null, null);

        var productIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(Collectors.toSet());
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
                .filter((accessControl) -> accessControl.getClazzId() == product.getId())
                .findFirst()
                .ifPresentOrElse(
                        accessControl -> product.setAccountGroup(accessControl.getAccountGroup()),
                        () -> {
                            throw new AccessControlNotDefinedException("Unable to embed AccountGroup to Product [" + product.getId() + ": " + product.getCode());
                        }
                );
        return product;
    }

    public ProductDto save(ProductDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException("Product must be associated to an Account Group");

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
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (pantryItemRepository.countPantryItem(id) > 0)
            throw new DatabaseConstraintException("Product can not be removed. It is referred in one or more pantry items.");

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
