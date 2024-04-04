package com.fcastro.purchaseservice.purchase;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.purchaseservice.config.PurchaseCompleteEventProducer;
import com.fcastro.purchaseservice.exception.NoItemToPurchaseException;
import com.fcastro.purchaseservice.exception.PurchaseAlreadyProcessedException;
import com.fcastro.purchaseservice.exception.PurchaseItemsMissingException;
import com.fcastro.purchaseservice.purchaseItem.PurchaseItemService;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.core.model.AccessControlDto;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository repository;
    private final PurchaseItemService purchaseItemService;
    private final ModelMapper modelMapper;
    private final PurchaseCompleteEventProducer eventProducer;
    private final AuthorizationHandler authorizationHandler;

    public PurchaseService(PurchaseRepository repository, PurchaseItemService purchaseItemService, ModelMapper modelMapper, PurchaseCompleteEventProducer eventProducer, AuthorizationHandler authorizationHandler) {
        this.repository = repository;
        this.purchaseItemService = purchaseItemService;
        this.modelMapper = modelMapper;
        this.eventProducer = eventProducer;
        this.authorizationHandler = authorizationHandler;
    }

    //List purchase orders where all pantries in it are acc"ssible the user has access.
    public List<PurchaseDto> listPurchaseOrder(String email) {
        var pantryIds = getPantryIdList(email);
        var entities = repository.findAllOrderByDescCreateAt(pantryIds);
        return convertToDto(entities);
    }

    public PurchaseDto getOpenPurchaseOrder(String email) {
        //return existing and pending purchase order
        var pantryIds = getPantryIdList(email);
        var purchase = convertToDto(repository.getPending(pantryIds));
        if (purchase != null) {
            var items = purchaseItemService.listPurchaseByCategory(purchase.getId(), null);
            purchase.setItems(items);
        }
        return purchase;
    }

    public PurchaseDto createPurchaseOrder(String email) {
        //return existing and pending purchase order
        var pantryIds = getPantryIdList(email);
        var entity = repository.getPending(pantryIds);
        if (entity != null) return convertToDto(entity);

        //check existence of items to purchase
        var pendingPurchase = purchaseItemService.listPendingPurchase(email);
        if (pendingPurchase == null || pendingPurchase.size() == 0) {
            throw new NoItemToPurchaseException("No items to Purchase at the moment.");
        }

        //create Purchase Order and associate all pending items
        entity = Purchase.builder().createdAt(LocalDateTime.now()).build();
        entity = repository.save(entity);

        purchaseItemService.updatePendingPurchaseItems(entity.getId());

        var dto = convertToDto(entity);
        dto.setItems(purchaseItemService.findAllByPurchaseId(entity.getId()));

        return dto;
    }

    public PurchaseDto closePurchaseOrder(String email, PurchaseDto dto) {

        var entity = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order was not found."));

        var pantryIds = getPantryIdList(email);
        if (pantryIds.stream().noneMatch((id) -> id == dto.getId())) {
            throw new AccessDeniedException("User is not allowed to delete this Purchase Order.");
        }

        if (dto.getItems() == null || dto.getItems().size() == 0) {
            throw new PurchaseItemsMissingException("Purchase items list is required to close a Purchase Order.");
        }

        if (entity.getProcessedAt() != null) {
            throw new PurchaseAlreadyProcessedException("Purchase Order had already been closed");
        }

        var itemList = purchaseItemService.processPurchasedItems(entity.getId(), dto.getItems());
        eventProducer.sendPurchaseCompleteEvent(itemList);

        entity.setProcessedAt(LocalDateTime.now());
        entity = repository.save(entity);

        return convertToDto(entity);
    }

    private Set<Long> getPantryIdList(String email) {
        var accessControlList = authorizationHandler.listAccessControl(email, "Pantry", null, null);
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

    private List<PurchaseDto> convertToDto(List<Purchase> entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, List.class);
    }
}
