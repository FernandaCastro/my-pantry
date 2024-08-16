package com.fcastro.pantryservice.pantryitem;

import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.Action;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.kafka.model.PurchaseEventDto;
import com.fcastro.pantryservice.event.PurchaseEventProducer;
import com.fcastro.pantryservice.exception.PantryAndProductAccountGroupInconsistentException;
import com.fcastro.pantryservice.exception.PantryNotActiveException;
import com.fcastro.pantryservice.exception.QuantityNotAvailableException;
import com.fcastro.pantryservice.pantry.Pantry;
import com.fcastro.pantryservice.pantry.PantryDto;
import com.fcastro.pantryservice.pantry.PantryType;
import com.fcastro.pantryservice.product.Product;
import com.fcastro.pantryservice.product.ProductDto;
import com.fcastro.pantryservice.product.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PantryItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PantryItemService.class);

    private final PantryItemRepository repository;
    private final ModelMapper modelMapper;
    private final PurchaseEventProducer eventProducer;
    private final ProductService productService;

    private static final int SEND_PURCHASE_EVENT_THRESHOLD = 50;

    public PantryItemService(PantryItemRepository pantryItemRepository, ModelMapper modelMapper, PurchaseEventProducer eventProducer, ProductService productService) {
        this.repository = pantryItemRepository;
        this.modelMapper = modelMapper;
        this.eventProducer = eventProducer;
        this.productService = productService;
    }

    public Optional<PantryItemDto> get(long pantryId, long productId) {
        return repository.findById(new PantryItemKey(pantryId, productId))
                .map(this::convertToDTO);
    }

    public List<PantryItemDto> getAll(long pantryId) {
        var listEntity = repository.findAllByPantryId(pantryId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Deprecated
    public List<PantryItemDto> getAllConsume(long pantryId) {
        var listEntity = repository.findAllToConsumeByPantryId(pantryId);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PantryItemDto> getAllConsume(List<Long> pantryIds) {
        var listEntity = repository.findAllToConsumeByPantryId(pantryIds);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PantryItemDto create(PantryItemDto dto) {
        //Check AccountGroups when adding new item:
        //Product can be in either in the same level or in higher group of the pantry, never in a lower level
        if (dto.getProduct().getAccountGroup().getId() == dto.getPantry().getAccountGroup().getId() || //pantry & product are in the same level group
                (dto.getPantry().getAccountGroup().getParentAccountGroup() != null && // pantry is in a lower level
                        (dto.getPantry().getAccountGroup().getParentAccountGroup().getId() == dto.getProduct().getAccountGroup().getId() // product in a high level group
                        ))) {

            var entity = repository.save(convertToEntity(dto));
            return convertToDTO(entity);
        }

        throw new PantryAndProductAccountGroupInconsistentException(MessageTranslator.getMessage("error.product.not.allowed", dto.getProduct().getCode()));
    }

    public PantryItemDto update(PantryItemDto dto) {
        var entity = repository.save(convertToEntity(dto));
        return convertToDTO(entity);
    }

    public void delete(long pantryId, long productId) {
        var pk = new PantryItemKey(pantryId, productId);
        repository.findById(pk)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.pantry.item.not.found")));

        repository.deleteById(pk);
    }

    public void deleteAllItems(long pantryId) {
        repository.deleteByPantryId(pantryId);
        //TODO: Notify Purchase
    }

    //Updates pantryProduct table and send ProductConsumedEvent
    @Transactional
    public List<PantryItemDto> consumePantryItem(Long pantryId, List<PantryItemConsumedDto> list) {

        return list.stream()
                .filter(item -> item.qty > 0)
                .map(item -> {
                    item.setPantryId(pantryId);
                    return consumePantryItem(item);
                })
                .collect(Collectors.toList());
    }

    public PantryItemDto consumePantryItem(PantryItemConsumedDto consumedDto) {
        var itemEntity = repository.findEagerByPantryIdAndProductId(consumedDto.getPantryId(), consumedDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.pantry.item.not.found")));

        if (!itemEntity.getPantry().getIsActive()) {
            throw new PantryNotActiveException(MessageTranslator.getMessage("error.pantry.not.active"));
        }

        if (itemEntity.getCurrentQty() < consumedDto.getQty()) {
            throw new QuantityNotAvailableException(MessageTranslator.getMessage("error.insufficient.quantity", itemEntity.getProduct().getCode(), String.valueOf(itemEntity.getCurrentQty())));
        }

        itemEntity.setCurrentQty(itemEntity.getCurrentQty() - consumedDto.getQty());

        if (PantryType.RECURRING.value.equals(itemEntity.getPantry().getType())) {
            itemEntity = processPurchaseNeed(itemEntity);
        }

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

        //IdalQty = 0 => (100%) No need of provisioning
        if (itemEntity.getIdealQty() <= 0 && itemEntity.getProvisionedQty() <= 0) return itemEntity;

        var currPlusProv = itemEntity.getCurrentQty() + itemEntity.getProvisionedQty();
        var provision = itemEntity.getIdealQty() - currPlusProv;
        var availabilityPercentage = calculateAvailability(itemEntity);

        // Availability is below SEND_PURCHASE_EVENT_THRESHOLD (50%)
        if (availabilityPercentage <= SEND_PURCHASE_EVENT_THRESHOLD) {

            if (provision == 0) return itemEntity;

            if (provision > 0)
                sendPurchaseEvent(Action.CREATE, itemEntity, provision);

            if (provision < 0)
                sendPurchaseEvent(Action.DELETE, itemEntity, (provision * -1));

            var provisionedQty = Math.max(itemEntity.getProvisionedQty() + provision, 0);

            itemEntity.setProvisionedQty(provisionedQty);
            itemEntity.setLastProvisioning((provisionedQty > 0) ? LocalDateTime.now() : null);

            return itemEntity;
        }

        // Availability is above SEND_PURCHASE_EVENT_THRESHOLD (50%) - No Provisioning need
        if (availabilityPercentage > SEND_PURCHASE_EVENT_THRESHOLD) {

            if (itemEntity.getProvisionedQty() > 0) {
                sendPurchaseEvent(Action.DELETE, itemEntity, itemEntity.getProvisionedQty());
                itemEntity.setProvisionedQty(0);
                itemEntity.setLastProvisioning(null);
            }
        }

        return itemEntity;
    }

    private double calculateAvailability(PantryItem itemEntity) {
        if (itemEntity.getCurrentQty() == 0 || itemEntity.getIdealQty() == 0) return 0;
        return ((Double.valueOf(itemEntity.getCurrentQty()) / Double.valueOf(itemEntity.getIdealQty())) * 100);
    }

    private void sendPurchaseEvent(Action action, PantryItem itemEntity, int provision) {
        var purchaseEventItemDto = PurchaseEventDto.builder()
                .action(action)
                .qtyProvisioned(provision)
                .build();
        enrichPurchaseItemDto(purchaseEventItemDto, itemEntity);
        eventProducer.send(purchaseEventItemDto);
    }

    public void processPurchaseCompleteEvent(List<PurchaseEventDto> purchasedList) {

        var exceptions = new HashMap<Serializable, Throwable>();
        purchasedList.forEach(
                (purchasedItem) -> {
                    try {
                        updatePantryItem(purchasedItem);
                    } catch (Exception ex) {
                        exceptions.put(purchasedItem, ex);
                    }
                }
        );

        if (exceptions.size() > 0) {
            LOGGER.error("Error occurred while processing a PurchaseCompleteEvent", exceptions);
            throw new EventProcessingException(MessageTranslator.getMessage("error.processing.purchase.complete.event"));
        }
    }

    private void updatePantryItem(PurchaseEventDto purchasedItem) {
        if (purchasedItem.getQtyPurchased() == 0) return;

        var entity = repository.findById(
                        PantryItemKey.builder()
                                .pantryId(purchasedItem.getPantryId())
                                .productId(purchasedItem.getProductId())
                                .build())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.pantry.item.not.found")));

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

    public List<PantryItemDto> createWizard(PantryDto pantry, List<PantryItemWizardDto> items) {
        List<PantryItemDto> list = new ArrayList<>();

        for (PantryItemWizardDto wizardItem : items) {
            var productSearch = ProductDto.builder()
                    .code(wizardItem.getCode())
                    .size(wizardItem.getSize())
                    .category(wizardItem.getCategory())
                    .accountGroup(pantry.getAccountGroup())
                    .build();

            //get existing product or create new product
            var product = productService.getOrCreate(productSearch);

            var pantryItem = PantryItemDto.builder()
                    .pantry(pantry)
                    .product(product)
                    .idealQty(wizardItem.getIdealQty())
                    .currentQty(wizardItem.getCurrentQty())
                    .build();

            //create new Pantry Item
            var newPantryItem = create(pantryItem);

            list.add(newPantryItem);
        }

        return list;
    }

    private void enrichPurchaseItemDto(PurchaseEventDto dto, PantryItem itemEntity) {
        dto.setPantryId(itemEntity.getPantry().getId());
        dto.setPantryName(itemEntity.getPantry().getName());
        dto.setProductId(itemEntity.getProduct().getId());
    }

    private PantryItemDto convertToDTO(PantryItem entity) {
        if (entity == null) return null;

        ProductDto product = null;
        if (entity.getProduct() != null) {
            product = ProductDto.builder()
                    .id(entity.getProduct().getId())
                    .code(entity.getProduct().getCode())
                    .description(entity.getProduct().getDescription())
                    .size(entity.getProduct().getSize())
                    .build();
        }

        PantryDto pantry = null;
        if (entity.getPantry() != null) {
            pantry = PantryDto.builder()
                    .id(entity.getPantry().getId())
                    .name(entity.getPantry().getName())
                    .type(entity.getPantry().getType())
                    .isActive(entity.getPantry().getIsActive())
                    .build();
        }

        return PantryItemDto.builder()
                .idealQty(entity.getIdealQty())
                .currentQty(entity.getCurrentQty())
                .provisionedQty(entity.getProvisionedQty())
                .lastProvisioning(entity.getLastProvisioning())
                .product(product)
                .pantry(pantry)
                .build();
    }

    private PantryItem convertToEntity(PantryItemDto dto) {
        if (dto == null) return null;

        return PantryItem.builder()
                .id(PantryItemKey.builder()
                        .pantryId(dto.getPantry() != null ? dto.getPantry().getId() : null)
                        .productId(dto.getProduct() != null ? dto.getProduct().getId() : null)
                        .build())
                .pantry(dto.getPantry() != null ? Pantry.builder().id(dto.getPantry().getId()).build() : null)
                .product(dto.getProduct() != null ? Product.builder().id(dto.getProduct().getId()).build() : null)
                .idealQty(dto.getIdealQty())
                .currentQty(dto.getCurrentQty())
                .provisionedQty(dto.getProvisionedQty())
                .lastProvisioning(dto.getLastProvisioning())
                .build();
    }

}
