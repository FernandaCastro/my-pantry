package com.fcastro.purchase.purchaseItem;

import com.fcastro.kafka.model.PurchaseEventItemDto;
import com.fcastro.purchase.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository repository;
    private final ModelMapper modelMapper;

    public PurchaseItemService(PurchaseItemRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public void processPurchaseEvent(PurchaseEventItemDto dto) {
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

    public List<PurchaseEventItemDto> processPurchasedItems(Long purchaseId, List<PurchaseItemDto> purchasedItems) {

        var purchaseEventList = new ArrayList<PurchaseEventItemDto>();
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
                    .productId(purchasedItem.getProductId())
                    .productCode(purchasedItem.getProductCode())
                    .productDescription(purchasedItem.getProductDescription())
                    .productSize(purchasedItem.getProductSize())
                    .build();
            repository.save(newEntity);
        }
    }

    private PurchaseEventItemDto convertToItemDto(PurchaseItem entity) {
        return PurchaseEventItemDto.builder()
                .pantryId(entity.getPantryId())
                .pantryName(entity.getPantryName())
                .productId(entity.getProductId())
                .productCode(entity.getProductCode())
                .productDescription(entity.getProductDescription())
                .productSize(entity.getProductSize())
                .qtyProvisioned(entity.getQtyProvisioned())
                .qtyPurchased(entity.getQtyPurchased())
                .build();
    }

    private PurchaseItem convertToEntity(PurchaseEventItemDto dto) {
        return PurchaseItem.builder()
                .pantryId(dto.getPantryId())
                .pantryName(dto.getPantryName())
                .productId(dto.getProductId())
                .productCode(dto.getProductCode())
                .productDescription(dto.getProductDescription())
                .productSize(dto.getProductSize())
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
        return modelMapper.map(entities, List.class);
    }

    private PurchaseItemDto convertToDto(PurchaseItem entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PurchaseItemDto.class);
    }
}
