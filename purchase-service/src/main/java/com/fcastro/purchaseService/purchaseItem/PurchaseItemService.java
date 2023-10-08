package com.fcastro.purchaseService.purchaseItem;

import com.fcastro.events.PurchaseEventDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository repository;
    private final ModelMapper modelMapper;

    public PurchaseItemService(PurchaseItemRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public void processPurchaseEvent(PurchaseEventDto dto) {
        if (dto == null) return;
        repository.save(convertToEntity(dto));
    }

    public int countPendingPurchase() {
        return repository.countPendingPurchase();
    }

    public int updatePendingPurchase(Long purchaseId) {
        return repository.updatePendingPurchase(purchaseId);
    }

    public List<PurchaseItemDto> findAllByPurchaseId(Long purchaseId) {
        return convertToDto(repository.findAllByPurchaseId(purchaseId));
    }

    public PurchaseItemDto save(PurchaseItemDto dto) {
        if (dto == null) return null;
        var entity = repository.save(convertToEntity(dto));
        return convertToDto(entity);
    }

    private PurchaseItem convertToEntity(PurchaseEventDto dto) {

        return PurchaseItem.builder()
                .pantryId(dto.getPantryId())
                .pantryName(dto.getPantryName())
                .productId(dto.getProductId())
                .productDescription(dto.getProductDescription())
                .productSize(dto.getProductSize())
                .quantity(dto.getQuantity())
                .isPurchased(dto.isPurchased())
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
