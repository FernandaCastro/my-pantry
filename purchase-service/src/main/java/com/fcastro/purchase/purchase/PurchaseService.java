package com.fcastro.purchase.purchase;

import com.fcastro.purchase.config.EventProducer;
import com.fcastro.purchase.exception.NoItemToPurchaseException;
import com.fcastro.purchase.exception.PurchaseAlreadyProcessedException;
import com.fcastro.purchase.exception.ResourceNotFoundException;
import com.fcastro.purchase.purchaseItem.PurchaseItemService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<PurchaseDto> listPurchaseOrder() {
        var entities = repository.findAllOrderByDescCreateAt();
        return convertToDto(entities);
    }


    @Transactional
    public PurchaseDto getOrCreatePurchaseOrder() {
        //return existing and pending purchase order
        var entity = repository.getPending();
        if (entity != null) return convertToDto(entity);


        //check existence of items to purchase
        if (purchaseItemService.countPendingPurchase() == 0) {
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

    public PurchaseDto closePurchaseOrder(PurchaseDto dto) {
        var entity = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order was not found."));

        if (entity.getProcessedAt() != null) {
            throw new PurchaseAlreadyProcessedException("Purchase Order had already been closed");
        }

        var itemList = purchaseItemService.processPurchasedItems(entity.getId(), dto.getItems());
        eventProducer.sendPurchaseCompleteEvent(itemList);

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

    private List<PurchaseDto> convertToDto(List<Purchase> entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, List.class);
    }
}
