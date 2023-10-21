package com.fcastro.purchaseService.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCreateEvent;
import com.fcastro.purchaseService.purchaseItem.PurchaseItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    private final PurchaseItemService purchaseItemService;

    private final KafkaConfigData kafkaConfigData;

    public EventConsumer(PurchaseItemService purchaseItemService, KafkaConfigData kafkaConfigData) {
        this.purchaseItemService = purchaseItemService;
        this.kafkaConfigData = kafkaConfigData;
    }

    //TODO: HOW TO define <topics> getting value from configuration files?
    @KafkaListener(topics = "purchaseCreateTopic", containerFactory = "kafkaListenerContainerFactory")
    void listener(PurchaseCreateEvent event) {

        if (event.getItem() == null) {
            LOGGER.error("Event {} received, but attribute data is null.", event.getClass().getSimpleName());
            return;
        }

        LOGGER.info("Received from {}: {}", kafkaConfigData.getPurchaseCreateTopic(), event.getItem().toString());

        purchaseItemService.processPurchaseEvent(event.getItem());
    }
}
