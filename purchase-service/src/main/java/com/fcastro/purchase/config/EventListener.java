package com.fcastro.purchase.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCreateEvent;
import com.fcastro.purchase.purchaseItem.PurchaseItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    private final PurchaseItemService purchaseItemService;

    private final KafkaConfigData kafkaConfigData;

    public EventListener(PurchaseItemService purchaseItemService, KafkaConfigData kafkaConfigData) {
        this.purchaseItemService = purchaseItemService;
        this.kafkaConfigData = kafkaConfigData;
    }

    //TODO: HOW TO define <topics> getting value from configuration files?
    @KafkaListener(topics = "purchaseCreateTopic", containerFactory = "kafkaListenerContainerFactory")
    protected void listener(PurchaseCreateEvent event) {

        if (event.getItem() == null) {
            LOGGER.error("PurchaseCreateEvent received, but attribute data is null.");
            return;
        }

        LOGGER.info("Event Received: Topic[{}], Data[{}]",
                kafkaConfigData.getPurchaseCreateTopic(),
                event.toString()
        );

        purchaseItemService.processPurchaseEvent(event.getItem());
    }
}
