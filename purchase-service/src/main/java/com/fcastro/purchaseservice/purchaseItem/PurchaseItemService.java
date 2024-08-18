package com.fcastro.purchaseservice.purchaseItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.Action;
import com.fcastro.kafka.model.PurchaseEventDto;
import com.fcastro.purchaseservice.product.Product;
import com.fcastro.purchaseservice.product.ProductService;
import com.fcastro.purchaseservice.supermarket.SupermarketService;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.core.model.AccessControlDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository repository;
    private final ProductService productService;
    private final SupermarketService supermarketService;
    private final ModelMapper modelMapper;
    private final AuthorizationHandler authorizationHandler;
    private ObjectMapper jsonMapper;

    public PurchaseItemService(PurchaseItemRepository repository, ProductService productService, SupermarketService supermarketService, ModelMapper modelMapper, AuthorizationHandler authorizationHandler) {
        this.repository = repository;
        this.productService = productService;
        this.supermarketService = supermarketService;
        this.modelMapper = modelMapper;
        this.authorizationHandler = authorizationHandler;
        this.jsonMapper = new ObjectMapper();
    }

    public void processPurchaseEvent(PurchaseEventDto dto) {
        if (dto == null) return;

        switch (dto.getAction()) {
            case CREATE:
                processCreateProvisioningEvent(dto);
                return;

            case DELETE:
                processDeleteProvisioningEvent(dto);
        }
    }

    private void processCreateProvisioningEvent(PurchaseEventDto dto) {

        productService.get(dto.getProductId()).orElseThrow(
                () -> new ResourceNotFoundException(MessageTranslator.getMessage("error.product.not.found")));

        //It's in an open order => increase provisionedQty and signalise
        var openOrderItem = repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(dto.getPantryId(), dto.getProductId());
        if (openOrderItem != null) {
            openOrderItem.setQtyProvisioned(openOrderItem.getQtyProvisioned() + dto.getQtyProvisioned());
            //TODO: Signalise qty changed
            repository.save(openOrderItem);
            return;
        }

        //It's not in an order => increase provisionedQty
        var noOrderItem = repository.findByPantryIdAndProductIdAndNoPurchaseOrder(dto.getPantryId(), dto.getProductId());
        if (noOrderItem != null) {
            noOrderItem.setQtyProvisioned(noOrderItem.getQtyProvisioned() + dto.getQtyProvisioned());
            repository.save(noOrderItem);
            return;
        }

        //It doesn't exist => create new provisioning
        var newProvisioning = convertToEntity(dto);
        repository.save(newProvisioning);
    }


    private void processDeleteProvisioningEvent(PurchaseEventDto dto) {

        //It's in an open order => decrease provisionedQty and signalise
        var openOrderItem = repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(dto.getPantryId(), dto.getProductId());
        if (openOrderItem != null) {
            openOrderItem.setQtyProvisioned(Math.max(openOrderItem.getQtyProvisioned() - dto.getQtyProvisioned(), 0));
            //TODO: Signalise qty changed
            repository.save(openOrderItem);
            return;
        }

        //It's not in an order,
        //  provisionedQty  > 0 => decrease provisionedQty;
        //  provisionedQty <= 0 => delete the entire provisioning
        var noOrderItem = repository.findByPantryIdAndProductIdAndNoPurchaseOrder(dto.getPantryId(), dto.getProductId());
        if (noOrderItem != null) {
            if (noOrderItem.getQtyProvisioned() - dto.getQtyProvisioned() > 0) {
                noOrderItem.setQtyProvisioned(Math.max(noOrderItem.getQtyProvisioned() - dto.getQtyProvisioned(), 0));
                repository.save(noOrderItem);
                return;
            }
            repository.delete(noOrderItem);
        }
    }

    public List<PurchaseItemDto> listPendingPurchase(String email) {
        var pantryIds = getPantryIdList(email);
        return convertToDto(repository.listPendingPurchase(pantryIds));
    }

    public List<PurchaseItemDto> listPendingPurchase(Set<Long> pantryIds) {
        return convertToDto(repository.listPendingPurchase(pantryIds));
    }

    public List<PurchaseItemDto> listPendingPurchaseByCategory(String email, Set<Long> pantryIds, Long supermarketId) {
        //var pantryIds = getPantryIdList(email);
        var list = convertToDto(repository.listPendingPurchase(pantryIds));
        if (supermarketId == null || supermarketId == 0) return list;

        return categorize(list, supermarketId);
    }

    public List<PurchaseItemDto> listPurchaseByCategory(Long purchaseId, Long supermarketId) {
        var list = convertToDto(repository.findAllByPurchaseId(purchaseId));
        if (supermarketId == null || supermarketId == 0) return list;

        return categorize(list, supermarketId);
    }

    private Set<Long> getPantryIdList(String email) {
        var accessControlList = authorizationHandler.listAccessControl(email, "Pantry", null, null, null);
        return accessControlList.stream().map(AccessControlDto::getClazzId).collect(Collectors.toSet());
    }

    private List<PurchaseItemDto> categorize(List<PurchaseItemDto> list, Long supermarketId) {
        var map = list.stream()
                .collect(Collectors.groupingBy((item) ->
                        item.getProduct().getCategory() == null || Objects.equals(item.getProduct().getCategory(), "") ?
                                "Other" : item.getProduct().getCategory()
                ));

        var supermarket = supermarketService.get(supermarketId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.supermarket.not.found")));

        var categorized = new ArrayList<PurchaseItemDto>();
        supermarket.getCategories().forEach((c) -> {
            if (map.containsKey(c)) {
                categorized.addAll(map.get(c));
                map.remove(c);
            }
        });
        map.values().forEach(categorized::addAll);

        return categorized;
    }

    @Transactional
    public void updatePendingPurchaseItems(Long purchaseId, Set<Long> pantryIds) {
        repository.updatePendingPurchaseItems(purchaseId, pantryIds);
    }

    public List<PurchaseItemDto> findAllByPurchaseId(Long purchaseId) {
        return convertToDto(repository.findAllByPurchaseId(purchaseId));
    }

    public PurchaseItemDto save(PurchaseItemDto dto) {
        if (dto == null) return null;
        var entity = repository.save(convertToEntity(dto));
        return convertToDto(entity);
    }

    public List<PurchaseEventDto> processPurchasedItems(Long purchaseId, List<PurchaseItemDto> purchasedItems) {

        var purchaseEventList = new ArrayList<PurchaseEventDto>();
        if (purchasedItems == null || purchasedItems.size() == 0) return purchaseEventList;

        for (PurchaseItemDto purchasedItem : purchasedItems) {

            var entity = repository.findByIdAndPurchaseId(purchasedItem.getId(), purchaseId)
                    .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.item.not.in.purchase", purchasedItem.getProduct() != null ? purchasedItem.getProduct().getCode() : String.valueOf(purchasedItem.getId()))));

            entity.setQtyPurchased(purchasedItem.getQtyPurchased());
            repository.save(entity);

            processPendingProvisioning(entity);
            purchaseEventList.add(convertToItemDto(Action.UPDATE, entity));
        }

        return purchaseEventList;
    }

    private void processPendingProvisioning(PurchaseItem purchasedItem) {
        int qtyMissing = purchasedItem.getQtyProvisioned() - purchasedItem.getQtyPurchased();
        if (qtyMissing > 0) {
            //Create a new PurchaseItem with the missing quantity
            var newEntity = PurchaseItem.builder()
                    .qtyProvisioned(qtyMissing)
                    .pantryId(purchasedItem.getPantryId())
                    .pantryName(purchasedItem.getPantryName())
                    //.productId(purchasedItem.getProductId())
                    .product(Product.builder().id(purchasedItem.getProduct().getId()).build())
                    .build();
            repository.save(newEntity);
        }
    }

    private PurchaseEventDto convertToItemDto(Action action, PurchaseItem entity) {
        return PurchaseEventDto.builder()
                .action(action)
                .pantryId(entity.getPantryId())
                .pantryName(entity.getPantryName())
                .productId(entity.getProduct().getId())
                .qtyProvisioned(entity.getQtyProvisioned())
                .qtyPurchased(entity.getQtyPurchased())
                .build();
    }

    private PurchaseItem convertToEntity(PurchaseEventDto dto) {
        return PurchaseItem.builder()
                .pantryId(dto.getPantryId())
                .pantryName(dto.getPantryName())
                .product(Product.builder().id(dto.getProductId()).build())
                .qtyProvisioned(dto.getQtyProvisioned())
                .qtyPurchased(dto.getQtyPurchased())
                .build();
    }

    private PurchaseItem convertToEntity(PurchaseItemDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, PurchaseItem.class);
    }

    private List<PurchaseItemDto> convertToDto(List<PurchaseItem> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PurchaseItemDto convertToDto(PurchaseItem entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PurchaseItemDto.class);
    }
}
