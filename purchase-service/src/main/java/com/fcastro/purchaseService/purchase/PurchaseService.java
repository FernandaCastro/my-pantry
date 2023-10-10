package com.fcastro.purchaseService.purchase;

import com.fcastro.purchaseService.config.EventProducer;
import com.fcastro.purchaseService.exception.NoItemToPurchase;
import com.fcastro.purchaseService.exception.PurchaseAlreadyProcessedException;
import com.fcastro.purchaseService.exception.ResourceNotFoundException;
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
    private final EventProducer eventProducer;

    public PurchaseService(PurchaseRepository repository, PurchaseItemService purchaseItemService, ModelMapper modelMapper, EventProducer eventProducer) {
        this.repository = repository;
        this.purchaseItemService = purchaseItemService;
        this.modelMapper = modelMapper;
        this.eventProducer = eventProducer;
    }


    @Transactional
    public PurchaseDto getOrCreatePurchaseOrder() {
        //return existing and pending purchase order
        var entity = repository.getPending();
        if (entity != null) return convertToDto(entity);


        //check existence of items to purchase
        if (purchaseItemService.countPendingPurchase() == 0) {
            throw new NoItemToPurchase("No items to Purchase at the moment.");
        }

        //create Purchase Order and associate all pending items
        entity = Purchase.builder().createdAt(LocalDateTime.now()).build();
        entity = repository.save(entity);

        purchaseItemService.updatePendingPurchaseItems(entity.getId());

        var dto = convertToDto(entity);
        dto.setItems(purchaseItemService.findAllByPurchaseId(entity.getId()));

        return dto;
    }

    public PurchaseDto closePurchaseOrder(PurchaseDto dto) {
        var entity = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order was not found."));

        if (entity.getProcessedAt() != null) {
            throw new PurchaseAlreadyProcessedException("Purchase Order had already been closed");
        }

        var ItemList = purchaseItemService.processPurchasedItems(entity.getId(), dto.getItems());
        eventProducer.sendPurchaseCompleteEvent(ItemList);

        entity.setProcessedAt(LocalDateTime.now());
        entity = repository.save(entity);

        return convertToDto(entity);
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
