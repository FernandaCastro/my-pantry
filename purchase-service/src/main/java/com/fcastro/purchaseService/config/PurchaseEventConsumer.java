package com.fcastro.purchaseService.config;

import com.fcastro.events.PurchaseEvent;
import com.fcastro.purchaseService.purchaseItem.PurchaseItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PurchaseEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEventConsumer.class);

    private final PurchaseItemService purchaseItemService;

    public PurchaseEventConsumer(PurchaseItemService purchaseItemService) {
        this.purchaseItemService = purchaseItemService;
    }

    @KafkaListener(topics = PurchaseEventConfig.PURCHASE_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    void listener(PurchaseEvent event) {

        LOGGER.info("Received: {}", event.getItem().toString());

        purchaseItemService.processPurchaseEvent(event.getItem());
    }
}
