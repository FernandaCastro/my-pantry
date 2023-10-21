package com.fcastro.pantryService.pantryItem;

import com.fcastro.kafka.model.PurchaseItemDto;
import com.fcastro.pantryService.config.EventProducer;
import com.fcastro.pantryService.exception.PantryNotActiveException;
import com.fcastro.pantryService.exception.QuantityNotAvailableException;
import com.fcastro.pantryService.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PantryItemService {

    private final PantryItemRepository repository;
    private final ModelMapper modelMapper;
    private final EventProducer eventProducer;

    private static final int SEND_PURCHASE_EVENT_THRESHOLD = 50;

    public PantryItemService(PantryItemRepository pantryItemRepository, ModelMapper modelMapper, EventProducer eventProducer) {
        this.repository = pantryItemRepository;
        this.modelMapper = modelMapper;
        this.eventProducer = eventProducer;
    }

    public Optional<PantryItemDto> get(long pantryId, long productId) {
        return repository.findById(new PantryItemKey(pantryId, productId))
                .map(this::convertToDTO);
    }

    public List<PantryItemDto> getAll(long pantryId) {
        var listEntity = repository.findAllByPantryId(pantryId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PantryItemDto save(PantryItemDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long pantryId, long productId) {
        var pk = new PantryItemKey(pantryId, productId);
        repository.findById(pk)
                .orElseThrow(() -> new ResourceNotFoundException("Pantry Product not found"));

        repository.deleteById(pk);
    }

    //Updates pantryProduct table and send ProductConsumedEvent
    @Transactional
    public PantryItemDto consumePantryItem(PantryItemConsumedDto consumedDto) {
        var itemEntity = repository.findEagerByPantryIdAndProductId(consumedDto.getPantryId(), consumedDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Pantry Product not found"));

        if (!itemEntity.getPantry().getIsActive()) {
            throw new PantryNotActiveException("Pantry is not active.");
        }

        if (itemEntity.getCurrentQty() < consumedDto.getQty()) {
            throw new QuantityNotAvailableException(itemEntity.getCurrentQty() + " quantity available in Pantry.");
        }

        //Update Pantry Item Inventory
        itemEntity.setCurrentQty(itemEntity.getCurrentQty() - consumedDto.getQty());

        //Provision Purchase need
        var provision = sendPurchaseCreateEvent(itemEntity);
        if (provision > 0) {
            itemEntity.setProvisionedQty(itemEntity.getProvisionedQty() + provision);
            itemEntity.setLastProvisioning(LocalDateTime.now());
        }

        repository.save(itemEntity);

        return convertToDTO(itemEntity);
    }

    //Calculate the Purchase Need (Send PurchaseCreateEvent)
    // 1. when currentQty is below PURCHASE_THRESHOLD %  AND
    // 2. (currentQty + provisionedQty) < ideal_qty
    private int sendPurchaseCreateEvent(PantryItem itemEntity) {

        // 1. when currentQty is below PURCHASE_THRESHOLD %
        var currPercentage = (100 * itemEntity.getCurrentQty()) / itemEntity.getIdealQty();

        // 2. (currentQty + provisionedQty) < ideal_qty
        var provisioned = itemEntity.getCurrentQty() + itemEntity.getProvisionedQty();
        var provision = provisioned < itemEntity.getIdealQty() ? itemEntity.getIdealQty() - provisioned : 0;

        if (currPercentage <= SEND_PURCHASE_EVENT_THRESHOLD && provision > 0) {
            var purchaseDto = PurchaseItemDto.builder().qtyProvisioned(provision).build();
            enrichPurchaseItemDto(purchaseDto, itemEntity);
            eventProducer.send(purchaseDto);
            return provision;
        }
        return 0;
    }

    public void processPurchaseCompleteEvent(List<PurchaseItemDto> list) {
        for (PurchaseItemDto item : list) {
            if (item.getQtyPurchased() > 0) {
                updatePantryItem(item);
            }
        }
    }

    private void updatePantryItem(PurchaseItemDto item) {
        var entity = repository.findById(PantryItemKey.builder().pantryId(item.getPantryId()).productId(item.getProductId()).build()).get();
        if (entity == null) return;

        entity.setCurrentQty(entity.getCurrentQty() + item.getQtyPurchased());

        var provisioned = item.getQtyPurchased() >= entity.getProvisionedQty() ? 0 : entity.getProvisionedQty() - item.getQtyPurchased();
        entity.setProvisionedQty(provisioned);

        if (provisioned == 0) entity.setLastProvisioning(null);

        repository.save(entity);
    }


    private void enrichPurchaseItemDto(PurchaseItemDto dto, PantryItem itemEntity) {
        dto.setPantryId(itemEntity.getPantry().getId());
        dto.setPantryName(itemEntity.getPantry().getName());

        dto.setProductId(itemEntity.getProduct().getId());
        dto.setProductDescription(itemEntity.getProduct().getDescription());
        dto.setProductSize(itemEntity.getProduct().getSize());
    }

    private PantryItemDto convertToDTO(PantryItem entity) {
        if (entity == null) return null;

        return PantryItemDto.builder()
                .pantryId(entity.getPantryId())
                .productId(entity.getProductId())
                .idealQty(entity.getIdealQty())
                .currentQty(entity.getCurrentQty())
                .provisionedQty(entity.getProvisionedQty())
                .lastProvisioning(entity.getLastProvisioning())
                .build();
    }

    private PantryItem convertToEntity(PantryItemDto dto) {
        if (dto == null) return null;

        return PantryItem.builder()
                .pantryId(dto.getPantryId())
                .productId(dto.getProductId())
                .idealQty(dto.getIdealQty())
                .currentQty(dto.getCurrentQty())
                .provisionedQty(dto.getProvisionedQty())
                .lastProvisioning(dto.getLastProvisioning())
                .build();
    }

}
