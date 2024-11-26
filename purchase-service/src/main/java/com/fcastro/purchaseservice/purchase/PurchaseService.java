package com.fcastro.purchaseservice.purchase;

import com.fcastro.commons.config.MessageTranslator;
import com.fcastro.commons.exception.ResourceNotFoundException;
import com.fcastro.kafka.model.AccountEventDto;
import com.fcastro.purchaseservice.event.PurchaseCompleteEventProducer;
import com.fcastro.purchaseservice.exception.NoItemToPurchaseException;
import com.fcastro.purchaseservice.exception.PurchaseAlreadyProcessedException;
import com.fcastro.purchaseservice.exception.PurchaseItemsMissingException;
import com.fcastro.purchaseservice.product.ProductDto;
import com.fcastro.purchaseservice.purchaseItem.PurchaseItem;
import com.fcastro.purchaseservice.purchaseItem.PurchaseItemDto;
import com.fcastro.purchaseservice.purchaseItem.PurchaseItemService;
import com.fcastro.security.authorization.AuthorizationClient;
import com.fcastro.security.modelclient.AccessControlDto;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository repository;
    private final PurchaseItemService purchaseItemService;
    private final ModelMapper modelMapper;
    private final PurchaseCompleteEventProducer eventProducer;
    private final AuthorizationClient authorizationHandler;

    public PurchaseService(PurchaseRepository repository, PurchaseItemService purchaseItemService, ModelMapper modelMapper, PurchaseCompleteEventProducer eventProducer, AuthorizationClient authorizationHandler) {
        this.repository = repository;
        this.purchaseItemService = purchaseItemService;
        this.modelMapper = modelMapper;
        this.eventProducer = eventProducer;
        this.authorizationHandler = authorizationHandler;
    }

    //List purchase orders where all pantries in it are accessible by the user.
    public List<PurchaseDto> listPurchaseOrder(String email, Set<Long> pantryIds) {
        var entities = repository.findAllOrderByDescCreateAt(pantryIds);
        return convertToDto(entities);
    }

    public PurchaseDto getOpenPurchaseOrder(String email, Set<Long> pantryIds) {
        //return existing and pending purchase order
        var purchase = convertToDto(repository.getPending(pantryIds));
        if (purchase != null) {
            var items = purchaseItemService.listPurchaseByCategory(purchase.getId(), null);
            purchase.setItems(items);
        }
        return purchase;
    }

    public PurchaseDto createPurchaseOrder(String email, Set<Long> pantryIds) {
        //return existing and pending purchase order
        var entity = repository.getPending(pantryIds);
        if (entity != null) return convertToDto(entity);

        //check existence of items to purchase
        var pendingPurchase = purchaseItemService.listPendingPurchase(pantryIds);
        if (pendingPurchase == null || pendingPurchase.size() == 0) {
            throw new NoItemToPurchaseException(MessageTranslator.getMessage("error.no.item.to.purchase"));
        }

        //create Purchase Order and associate all pending items
        entity = Purchase.builder().createdAt(LocalDateTime.now()).build();
        entity = repository.save(entity);

        purchaseItemService.updatePendingPurchaseItems(entity.getId(), pantryIds);

        var dto = convertToDto(entity);
        dto.setItems(purchaseItemService.findAllByPurchaseId(entity.getId()));

        return dto;
    }

    public PurchaseDto closePurchaseOrder(String email, PurchaseDto dto) {

        var entity = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.purchase.not.found")));

        //compare all items' pantries to the access allowed to the user
        var accessList = getAllPantriesAllowedToUser(email);
        var allMatch = entity.getItems().stream()
                .map(PurchaseItem::getPantryId)
                .allMatch(new HashSet<>(accessList)::contains);

        if (!allMatch) {
            throw new AccessDeniedException(MessageTranslator.getMessage("error.purchase.close.not.allowed"));
        }

        if (dto.getItems() == null || dto.getItems().size() == 0) {
            throw new PurchaseItemsMissingException(MessageTranslator.getMessage("error.purchase.list.required"));
        }

        if (entity.getProcessedAt() != null) {
            throw new PurchaseAlreadyProcessedException(MessageTranslator.getMessage("error.purchase.already.closed"));
        }

        var itemList = purchaseItemService.processPurchasedItems(entity.getId(), dto.getItems());
        eventProducer.sendPurchaseCompleteEvent(entity.getId(), itemList);

        entity.setProcessedAt(LocalDateTime.now());
        entity = repository.save(entity);

        return convertToDto(entity);
    }

    //When an Account is deleted all data related to that account will be deleted.
    @Transactional(rollbackFor = Exception.class) //Rollback will also occur for checked exceptions
    public void delete(AccountEventDto eventDto) {
        purchaseItemService.delete(eventDto.getPantryIds());
        repository.deleteOrphans();
    }

    private Set<Long> getAllPantriesAllowedToUser(String email) {
        var accessControlList = authorizationHandler.listAccessControl(email, "Pantry", null, null, null);
        return accessControlList.stream().map(AccessControlDto::getClazzId).collect(Collectors.toSet());
    }

    private Purchase convertToEntity(PurchaseDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Purchase.class);
    }

    private PurchaseDto convertToDto(Purchase entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PurchaseDto.class);
    }

    private List<PurchaseDto> convertToDto(List<Purchase> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(entity -> PurchaseDto.builder()
                        .id(entity.getId())
                        .createdAt(entity.getCreatedAt())
                        .processedAt(entity.getProcessedAt())
                        //.items(convertItemToDto(entity.getItems()))
                        .build())
                .collect(Collectors.toList());

    }

    private List<PurchaseItemDto> convertItemToDto(List<PurchaseItem> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(entity ->
                        PurchaseItemDto.builder()
                                .id(entity.getId())
                                .pantryId(entity.getPantryId())
                                .pantryName(entity.getPantryName())
                                .product(ProductDto.builder()
                                        .id(entity.getProduct().getId())
                                        .code(entity.getProduct().getCode())
                                        .description(entity.getProduct().getDescription())
                                        .category(entity.getProduct().getCategory())
                                        .build())
                                .qtyProvisioned(entity.getQtyProvisioned())
                                .qtyPurchased(entity.getQtyPurchased())
                                .build())
                .collect(Collectors.toList());
    }
}
