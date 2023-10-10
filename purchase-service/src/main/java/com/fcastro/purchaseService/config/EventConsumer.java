package com.fcastro.purchaseService.config;

import com.fcastro.events.PurchaseCreateEvent;
import com.fcastro.purchaseService.purchaseItem.PurchaseItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    private final PurchaseItemService purchaseItemService;

    public EventConsumer(PurchaseItemService purchaseItemService) {
        this.purchaseItemService = purchaseItemService;
    }

    @KafkaListener(topics = EventConfig.PURCHASE_CREATE_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    void listener(PurchaseCreateEvent event) {

        LOGGER.info("Received from {}: {}", EventConfig.PURCHASE_CREATE_TOPIC, event.getItem().toString());

        purchaseItemService.processPurchaseEvent(event.getItem());
    }
}
