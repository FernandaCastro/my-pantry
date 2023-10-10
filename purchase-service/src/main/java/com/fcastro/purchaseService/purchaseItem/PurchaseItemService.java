package com.fcastro.purchaseService.purchaseItem;

import com.fcastro.events.ItemDto;
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

    public void processPurchaseEvent(ItemDto dto) {
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

    public List<PurchaseItemDto> findAllByPurchaseId(Long purchaseId) {
        return convertToDto(repository.findAllByPurchaseId(purchaseId));
    }

    public PurchaseItemDto save(PurchaseItemDto dto) {
        if (dto == null) return null;
        var entity = repository.save(convertToEntity(dto));
        return convertToDto(entity);
    }

    public List<ItemDto> processPurchasedItems(Long purchaseId, List<PurchaseItemDto> listDto) {

        var itemDtoList = new ArrayList<ItemDto>();
        if (listDto == null || listDto.size() == 0) return itemDtoList;

        for (PurchaseItemDto dto : listDto) {

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

    private ItemDto convertToItemDto(PurchaseItem entity) {
        return ItemDto.builder()
                .pantryId(entity.getPantryId())
                .pantryName(entity.getPantryName())
                .productId(entity.getProductId())
                .productDescription(entity.getProductDescription())
                .productSize(entity.getProductSize())
                .qtyProvisioned(entity.getQtyProvisioned())
                .qtyPurchased(entity.getQtyPurchased())
                .build();
    }

    private PurchaseItem convertToEntity(ItemDto dto) {
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
