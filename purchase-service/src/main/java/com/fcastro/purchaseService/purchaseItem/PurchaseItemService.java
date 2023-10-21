package com.fcastro.purchaseService.purchaseItem;

import com.fcastro.kafka.model.PurchaseItemDto;
import com.fcastro.purchaseService.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    public void processPurchaseEvent(PurchaseItemDto dto) {
        if (dto == null) return;

        var entity = repository.findByPantryIdAndProductIdAndPurchaseIdIsNull(dto.getPantryId(), dto.getProductId());

        if (entity != null)
            entity.setQtyProvisioned(entity.getQtyProvisioned() + dto.getQtyProvisioned());
        else
            entity = convertToEntity(dto);

        repository.save(entity);
    }

    public int countPendingPurchase() {
        return repository.countPendingPurchase();
    }

    public int updatePendingPurchaseItems(Long purchaseId) {
        return repository.updatePendingPurchaseItems(purchaseId);
    }

    public List<com.fcastro.purchaseService.purchaseItem.PurchaseItemDto> findAllByPurchaseId(Long purchaseId) {
        return convertToDto(repository.findAllByPurchaseId(purchaseId));
    }

    public com.fcastro.purchaseService.purchaseItem.PurchaseItemDto save(com.fcastro.purchaseService.purchaseItem.PurchaseItemDto dto) {
        if (dto == null) return null;
        var entity = repository.save(convertToEntity(dto));
        return convertToDto(entity);
    }

    public List<PurchaseItemDto> processPurchasedItems(Long purchaseId, List<com.fcastro.purchaseService.purchaseItem.PurchaseItemDto> listDto) {

        var itemDtoList = new ArrayList<PurchaseItemDto>();
        if (listDto == null || listDto.size() == 0) return itemDtoList;

        for (com.fcastro.purchaseService.purchaseItem.PurchaseItemDto dto : listDto) {

            var entity = repository.findByIdAndPurchaseId(dto.getId(), purchaseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item id: " + dto.getId() + " is not in the Purchase list"));

            if (dto.getQtyPurchased() > 0) {
                entity.setQtyPurchased(dto.getQtyPurchased());
                repository.save(entity);
            }

            processPendingPurchase(entity);

            itemDtoList.add(convertToItemDto(entity));
        }

        return itemDtoList;
    }


    private void processPendingPurchase(PurchaseItem entity) {
        int qtyMissing = entity.getQtyProvisioned() - entity.getQtyPurchased();
        if (qtyMissing > 0) {
            //Create a new PurchaseItem with the missing quantity
            var newEntity = PurchaseItem.builder()
                    .qtyProvisioned(qtyMissing)
                    .pantryId(entity.getPantryId())
                    .pantryName(entity.getPantryName())
                    .productId(entity.getProductId())
                    .productDescription(entity.getProductDescription())
                    .productSize(entity.getProductSize())
                    .build();
            repository.save(newEntity);
        }
    }

    private PurchaseItemDto convertToItemDto(PurchaseItem entity) {
        return PurchaseItemDto.builder()
                .pantryId(entity.getPantryId())
                .pantryName(entity.getPantryName())
                .productId(entity.getProductId())
                .productDescription(entity.getProductDescription())
                .productSize(entity.getProductSize())
                .qtyProvisioned(entity.getQtyProvisioned())
                .qtyPurchased(entity.getQtyPurchased())
                .build();
    }

    private PurchaseItem convertToEntity(PurchaseItemDto dto) {
        return PurchaseItem.builder()
                .pantryId(dto.getPantryId())
                .pantryName(dto.getPantryName())
                .productId(dto.getProductId())
                .productDescription(dto.getProductDescription())
                .productSize(dto.getProductSize())
                .qtyProvisioned(dto.getQtyProvisioned())
                .qtyPurchased(dto.getQtyPurchased())
                .build();
    }

    private PurchaseItem convertToEntity(com.fcastro.purchaseService.purchaseItem.PurchaseItemDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, PurchaseItem.class);
    }

    private List<com.fcastro.purchaseService.purchaseItem.PurchaseItemDto> convertToDto(List<PurchaseItem> entities) {
        if (entities == null) return null;
        return modelMapper.map(entities, List.class);
    }

    private com.fcastro.purchaseService.purchaseItem.PurchaseItemDto convertToDto(PurchaseItem entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, com.fcastro.purchaseService.purchaseItem.PurchaseItemDto.class);
    }
}
