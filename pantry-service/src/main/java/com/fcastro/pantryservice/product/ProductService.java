package com.fcastro.pantryservice.product;

import com.fcastro.commons.config.MessageTranslator;
import com.fcastro.commons.exception.ResourceNotFoundException;
import com.fcastro.kafka.model.AccountEventDto;
import com.fcastro.kafka.model.Action;
import com.fcastro.kafka.model.ProductEventDto;
import com.fcastro.pantryservice.event.ProductEventProducer;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import com.fcastro.pantryservice.exception.RequestParamExpectedException;
import com.fcastro.pantryservice.pantryitem.PantryItemRepository;
import com.fcastro.security.authorization.AuthorizationClient;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import com.fcastro.security.modelclient.AccessControlDto;
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
    private final AuthorizationClient authorizationClient;

    public ProductService(ProductRepository repository, PantryItemRepository pantryItemRepository, ProductEventProducer productEventProducer, ModelMapper modelMapper, AuthorizationClient authorizationClient) {
        this.repository = repository;
        this.pantryItemRepository = pantryItemRepository;
        this.productEventProducer = productEventProducer;
        this.modelMapper = modelMapper;
        this.authorizationClient = authorizationClient;
    }

    public Optional<ProductDto> getEmbeddingAccountGroup(String email, long id) {
        var accessControlList = authorizationClient.listAccessControl(email, Product.class.getSimpleName(), id, null, null);
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

        var accessControlList = authorizationClient.listAccessControl(email, Product.class.getSimpleName(), null, groupId, null);
        var productIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(toSet());

        var productList = repository.findAllByCodeOrDescription(searchParam.toLowerCase(), productIds).stream()
                .map(this::convertToDTO).collect(Collectors.toList());

        productList = embedAccountGroup(productList, accessControlList);
        return productList;
    }

    public List<ProductDto> getAll(String email) {

        var accessControlList = authorizationClient.listAccessControl(email, Product.class.getSimpleName(), null, null, null);

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

        return save(dto);
    }

    private ProductDto save(ProductDto dto) {
        var entity = repository.save(convertToEntity(dto));
        authorizationClient.saveAccessControl(Product.class.getSimpleName(), entity.getId(), dto.getAccountGroup().getId());

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

    //It trys to get product from (1) the informed accountGroup or (2) its accountGroup.parent,
    //otherwise it creates new product on accountGroup.parent (this way it keeps single behavior)
    //TODO: does it scale? Use cache for access-control, account-group?
    public ProductDto getOrCreate(ProductDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.product.and.group.association.required"));

        //get all products this user has access to, based on the account group hierarchy (if child account group, then retrieve also products from parent accont group).
        var accessList = authorizationClient.listAccessControl(SecurityContextHolder.getContext().getAuthentication().getName(), "Product", null, dto.getAccountGroup().getId(), null);

        var productIds = accessList.stream()
                .map(AccessControlDto::getClazzId)
                .collect(toSet());

        var productList = repository.findAllByCode(dto.getCode(), productIds);

        //More than one product found, then return the product from (1) the informed account group
        if (productList.size() > 1) {
            var accessControl = accessList.stream()
                    .filter(ac -> ac.getAccountGroup().getId() == dto.getAccountGroup().getId())
                    .findFirst()
                    .orElse(null);

            if (accessControl == null)
                throw new RuntimeException("Inconsistence in Access Control found! "); //not found (not expcted to happen)

            //get and build the correct product
            var productFound = productList.stream()
                    .filter(p -> p.getId() == accessControl.getClazzId())
                    .findFirst();
            var product = convertToDTO(productFound.get());
            product.setAccountGroup(accessControl.getAccountGroup());
            return product;
        }

        //one product found, so append its account group (it can be the informed accountGroup or its parent)
        if (productList.size() == 1) {
            var product = convertToDTO(productList.get(0));

            //get the account group
            var accessControl = accessList.stream()
                    .filter(ac -> ac.getClazzId() == product.getId())
                    .findFirst();

            product.setAccountGroup(accessControl.get().getAccountGroup());
            return product;
        }

        //no product found, create product in the accountGroup.parent
        if (dto.getAccountGroup().getParentAccountGroup() != null) {
            dto.setAccountGroup(dto.getAccountGroup().getParentAccountGroup());
        }

        return save(dto);
    }

    //TODO: does it scale? Use cache for access-control, account-group?
    private void existsInAccountGroup(ProductDto dto) {
        //var productList = repository.findAllByCode(dto.getCode()); //This can become a huge list (code: "Rice")
        var accessList = authorizationClient.listAccessControl(SecurityContextHolder.getContext().getAuthentication().getName(), "Product", null, dto.getAccountGroup().getId(), null);

        var productIds = accessList.stream()
                .map(AccessControlDto::getClazzId)
                .collect(toSet());

        var productList = repository.findAllByCode(dto.getCode(), productIds);

        if (productList.size() > 0)
            throw new DatabaseConstraintException(MessageTranslator.getMessage("error.product.already.exists.in.group"));

//        var found = accessList.stream()
//                .map(AccessControlDto::getClazzId)
//                .anyMatch(productList.stream()
//                        .map(Product::getId)
//                        .collect(toSet())::contains);
//
//        if (found)
//            throw new DatabaseConstraintException(MessageTranslator.getMessage("error.product.already.exists.in.group"));
    }

    public ProductDto update(ProductDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException(MessageTranslator.getMessage("error.product.and.group.association.required"));

        var productExists = dto.getId() > 0 ? repository.findById(dto.getId()) : Optional.empty();
        var entity = repository.save(convertToEntity(dto));

        //Add to access control only when adding a new Product
        if (!productExists.isPresent()) {
            authorizationClient.saveAccessControl(Product.class.getSimpleName(), entity.getId(), dto.getAccountGroup().getId());
        }

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
        authorizationClient.deleteAccessControl(Product.class.getSimpleName(), id);

        productEventProducer.send(ProductEventDto.builder()
                .action(Action.DELETE)
                .id(id)
                .build());
    }

    //When deleting an Account all products associated to the Account will be deleted
    public void delete(AccountEventDto eventDto) {

        repository.deleteAllById(eventDto.getProductIds());
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
