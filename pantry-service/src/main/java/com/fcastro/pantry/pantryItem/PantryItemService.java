package com.fcastro.pantry.pantryItem;

import com.fcastro.kafka.model.PurchaseEventItemDto;
import com.fcastro.pantry.config.EventProducer;
import com.fcastro.pantry.exception.PantryNotActiveException;
import com.fcastro.pantry.exception.QuantityNotAvailableException;
import com.fcastro.pantry.exception.ResourceNotFoundException;
import com.fcastro.pantry.product.ProductDto;
import org.hibernate.LazyInitializationException;
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
    public List<PantryItemDto> consumePantryItem(Long pantryId, List<PantryItemConsumedDto> list) {

        return list.stream()
                .filter(item -> item.qty > 0)
                .map(item -> {
                    item.pantryId = pantryId;
                    return consumePantryItem(item);
                })
                .collect(Collectors.toList());
    }

    public PantryItemDto consumePantryItem(PantryItemConsumedDto consumedDto) {
        var itemEntity = repository.findEagerByPantryIdAndProductId(consumedDto.getPantryId(), consumedDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Pantry Product not found"));

        if (!itemEntity.getPantry().getIsActive()) {
            throw new PantryNotActiveException("Pantry is not active.");
        }

        if (itemEntity.getCurrentQty() < consumedDto.getQty()) {
            throw new QuantityNotAvailableException(itemEntity.getCurrentQty() + " quantity available in Pantry.");
        }

        itemEntity.setCurrentQty(itemEntity.getCurrentQty() - consumedDto.getQty());

        itemEntity = processPurchaseNeed(itemEntity);

        repository.save(itemEntity);
        return convertToDTO(itemEntity);
    }

    public List<PantryItemDto> processPurchaseNeed(Long pantryId){
        var items = repository.findAllByPantryId(pantryId);
        return items.stream().
                map(item -> {
                    item = processPurchaseNeed(item);
                    repository.save(item);
                    return convertToDTO(item);
                })
                .collect(Collectors.toList());
    }

    private PantryItem processPurchaseNeed(PantryItem itemEntity) {
        var usagePercentage = calculateUsagePercentage(itemEntity);
        var provision = calculateProvision(itemEntity);

        if (usagePercentage <= SEND_PURCHASE_EVENT_THRESHOLD && provision > 0) {
            sendPurchaseCreateEvent(itemEntity, provision);
            itemEntity.setProvisionedQty(itemEntity.getProvisionedQty() + provision);
            itemEntity.setLastProvisioning(LocalDateTime.now());
        }
        return itemEntity;
    }

    private long calculateUsagePercentage(PantryItem itemEntity) {
        if(itemEntity.getCurrentQty() == 0) return 0;
        return (100 * itemEntity.getCurrentQty()) / itemEntity.getIdealQty();
    }

    private int calculateProvision(PantryItem itemEntity) {
        var provisioned = itemEntity.getCurrentQty() + itemEntity.getProvisionedQty();
        return provisioned < itemEntity.getIdealQty() ? itemEntity.getIdealQty() - provisioned : 0;
    }

    private void sendPurchaseCreateEvent(PantryItem itemEntity, int provision) {
        var purchaseEventItemDto = PurchaseEventItemDto.builder().qtyProvisioned(provision).build();
        enrichPurchaseItemDto(purchaseEventItemDto, itemEntity);
        eventProducer.send(purchaseEventItemDto);
    }

    public void processPurchaseCompleteEvent(List<PurchaseEventItemDto> purchasedList) {
        for (PurchaseEventItemDto purchasedItem : purchasedList) {
            updatePantryItem(purchasedItem);
        }
    }

    private void updatePantryItem(PurchaseEventItemDto purchasedItem) {
        if (purchasedItem.getQtyPurchased() == 0) return;

        var entity = repository.findById(
                        PantryItemKey.builder()
                                .pantryId(purchasedItem.getPantryId())
                                .productId(purchasedItem.getProductId())
                                .build())
                .get();

        //TODO: What should be done when the PurchasedItem doesn't exist in the Pantry? Add to the Pantry?
        if (entity == null) return;

        entity.setCurrentQty(entity.getCurrentQty() + purchasedItem.getQtyPurchased());

        //all qty purchased
        if (purchasedItem.getQtyPurchased() >= entity.getProvisionedQty()) {
            entity.setProvisionedQty(0);
            entity.setLastProvisioning(null);
        } else {
            //there's still remaining qty to purchase
            entity.setProvisionedQty(entity.getProvisionedQty() - purchasedItem.getQtyPurchased());
        }

        repository.save(entity);
    }


    private void enrichPurchaseItemDto(PurchaseEventItemDto dto, PantryItem itemEntity) {
        dto.setPantryId(itemEntity.getPantry().getId());
        dto.setPantryName(itemEntity.getPantry().getName());

        dto.setProductId(itemEntity.getProduct().getId());
        dto.setProductCode(itemEntity.getProduct().getCode());
        dto.setProductDescription(itemEntity.getProduct().getDescription());
        dto.setProductSize(itemEntity.getProduct().getSize());
    }

    private PantryItemDto convertToDTO(PantryItem entity) {
        if (entity == null) return null;

        ProductDto product = null;
        try {
            if (entity.getProduct() != null) {
                product = ProductDto.builder()
                        .id(entity.getProduct().getId())
                        .code(entity.getProduct().getCode())
                        .description(entity.getProduct().getDescription())
                        .size(entity.getProduct().getSize())
                        .build();
            }
        } catch (LazyInitializationException ex) {
            product = ProductDto.builder()
                    .id(entity.getProductId())
                    .build();
        }

        return PantryItemDto.builder()
                .pantryId(entity.getPantryId())
                .productId(entity.getProductId())
                .idealQty(entity.getIdealQty())
                .currentQty(entity.getCurrentQty())
                .provisionedQty(entity.getProvisionedQty())
                .lastProvisioning(entity.getLastProvisioning())
                .product(product)
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
