package com.fcastro.pantry.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCompleteEvent;
import com.fcastro.pantry.pantryItem.PantryItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    private final PantryItemService pantryItemService;

    private final KafkaConfigData kafkaConfigData;

    public EventListener(PantryItemService pantryItemService, KafkaConfigData kafkaConfigData) {
        this.pantryItemService = pantryItemService;
        this.kafkaConfigData = kafkaConfigData;
    }

    //TODO: HOW TO define <topics> getting value from configuration files?
    @KafkaListener(topics = "purchaseCompleteTopic", containerFactory = "kafkaListenerContainerFactory")
    protected void listener(PurchaseCompleteEvent event) {

        if (event.getItems() == null) {
            LOGGER.error("purchaseCompleteTopic received, but attribute data is null.");
            return;
        }

        LOGGER.info("Event Received: Topic[{}], Data[{}]",
                kafkaConfigData.getPurchaseCompleteTopic(),
                event.getItems().toString()
        );

        pantryItemService.processPurchaseCompleteEvent(event.getItems());
    }
}
