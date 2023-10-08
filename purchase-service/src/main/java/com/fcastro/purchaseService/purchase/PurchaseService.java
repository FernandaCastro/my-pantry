package com.fcastro.purchaseService.purchase;

import com.fcastro.purchaseService.NoItemToPurchase;
import com.fcastro.purchaseService.purchaseItem.PurchaseItemService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PurchaseService {

    private final PurchaseRepository repository;
    private final PurchaseItemService purchaseItemService;
    private final ModelMapper modelMapper;

    public PurchaseService(PurchaseRepository repository, PurchaseItemService purchaseItemService, ModelMapper modelMapper) {
        this.repository = repository;
        this.purchaseItemService = purchaseItemService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PurchaseDto createPurchaseOrder() {
        if (purchaseItemService.countPendingPurchase() == 0) {
            throw new NoItemToPurchase("No items to Purchase at the moment.");
        }

        var entity = repository.save(Purchase.builder().createdAt(LocalDateTime.now()).build());
        purchaseItemService.updatePendingPurchase(entity.getId());

        var dto = convertToDto(entity);
        dto.setItems(purchaseItemService.findAllByPurchaseId(entity.getId()));

        return dto;
    }

    private Purchase convertToEntity(PurchaseDto dto) {

        if (dto == null) return null;
        return modelMapper.map(dto, Purchase.class);
    }

    private PurchaseDto convertToDto(Purchase entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, PurchaseDto.class);

    }
}
