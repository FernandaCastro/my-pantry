package com.fcastro.pantryService.config;

import com.fcastro.events.PurchaseCompleteEvent;
import com.fcastro.pantryService.pantryItem.PantryItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    private final PantryItemService pantryItemService;

    public EventConsumer(PantryItemService pantryItemService) {
        this.pantryItemService = pantryItemService;
    }

    @KafkaListener(topics = EventConfig.PURCHASE_COMPLETE_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    void listener(PurchaseCompleteEvent event) {

        LOGGER.info("Received from {}: {}", EventConfig.PURCHASE_COMPLETE_TOPIC, event.getItems().toString());

        pantryItemService.processPurchaseCompleteEvent(event.getItems());
    }
}
