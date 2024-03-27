package com.fcastro.purchaseservice.purchaseItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.kafka.event.PurchaseEventDto;
import com.fcastro.purchaseservice.exception.ResourceNotValidException;
import com.fcastro.purchaseservice.product.Product;
import com.fcastro.purchaseservice.properties.PropertiesService;
import com.fcastro.purchaseservice.properties.PropertyKey;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository repository;
    private final PropertiesService propertiesService;
    private final ModelMapper modelMapper;
    private ObjectMapper jsonMapper;

    public PurchaseItemService(PurchaseItemRepository repository, PropertiesService propertiesService, ModelMapper modelMapper) {
        this.repository = repository;
        this.propertiesService = propertiesService;
        this.modelMapper = modelMapper;
        this.jsonMapper = new ObjectMapper();
    }

    public void processPurchaseEvent(PurchaseEventDto dto) {
        if (dto == null) return;

        var entity = repository.findByPantryIdAndProductIdAndPurchaseIdIsNull(dto.getPantryId(), dto.getProductId());

        if (entity != null)
            entity.setQtyProvisioned(entity.getQtyProvisioned() + dto.getQtyProvisioned());
        else
            entity = convertToEntity(dto);

        repository.save(entity);
    }

    public List<PurchaseItemDto> listPendingPurchase() {
        return convertToDto(repository.listPendingPurchase());
    }

    public List<PurchaseItemDto> listPendingPurchaseByCategory(String supermarket) {
        var list = convertToDto(repository.listPendingPurchase());
        if (supermarket == null || Strings.isEmpty(supermarket)) return list;

        var categorized = categorize(list, supermarket);
        return categorized;
    }

    public List<PurchaseItemDto> listPurchaseByCategory(Long purchaseId, String supermarket) {
        var list = convertToDto(repository.findAllByPurchaseId(purchaseId));
        if (supermarket == null || Strings.isEmpty(supermarket)) return list;

        var categorized = categorize(list, supermarket);
        return categorized;
    }

    private List<PurchaseItemDto> categorize(List<PurchaseItemDto> list, String supermarket) {
        var map = list.stream()
                .collect(Collectors.groupingBy((item) ->
                        item.getProduct().getCategory() == null || item.getProduct().getCategory() == "" ?
                                "Other" : item.getProduct().getCategory()
                ));

        String propertyKey = supermarket.toLowerCase() + "." + PropertyKey.SUPERMARKET_CATEGORIES.key;
        var property = propertiesService.get(propertyKey)
                .orElseThrow(() -> new ResourceNotFoundException("Property " + propertyKey + " not found."));

        List<String> categories = new ArrayList<String>();

        try {
            categories = Arrays.asList(jsonMapper.readValue(property.getPropertyValue(), String[].class));
        } catch (JsonProcessingException e) {
            throw new ResourceNotValidException("Invalid Json value for property: " + PropertyKey.SUPERMARKET_CATEGORIES.key);
        }

        var categorized = new ArrayList<PurchaseItemDto>();
        categories.forEach((c) -> {
            if (map.containsKey(c)) {
                categorized.addAll(map.get(c));
                map.remove(c);
            }
        });
        map.values().forEach(l -> categorized.addAll(l));

        return categorized;
    }

    @Transactional
    public void updatePendingPurchaseItems(Long purchaseId) {
        repository.updatePendingPurchaseItems(purchaseId);
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
                    .orElseThrow(() -> new ResourceNotFoundException("Item id: " + purchasedItem.getId() + " is not in the Purchase list"));

            entity.setQtyPurchased(purchasedItem.getQtyPurchased());
            repository.save(entity);

            processPendingProvisioning(entity);
            purchaseEventList.add(convertToItemDto(entity));
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

    private PurchaseEventDto convertToItemDto(PurchaseItem entity) {
        return PurchaseEventDto.builder()
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
                .map(entity -> convertToDto(entity))
                .collect(Collectors.toList());
    }

    private PurchaseItemDto convertToDto(PurchaseItem entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PurchaseItemDto.class);
    }
}
