package com.fcastro.purchaseservice.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.event.PurchaseEvent;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.purchaseservice.purchaseItem.PurchaseItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PurchaseEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEventListener.class);

    private final PurchaseItemService purchaseItemService;

    private final KafkaConfigData kafkaConfigData;

    public PurchaseEventListener(PurchaseItemService purchaseItemService, KafkaConfigData kafkaConfigData) {
        this.purchaseItemService = purchaseItemService;
        this.kafkaConfigData = kafkaConfigData;
    }

    //TODO: HOW TO define <topics> getting value from configuration files?
    @KafkaListener(topics = "purchaseCreateTopic", containerFactory = "kafkaListenerContainerFactory")
    protected void listener(PurchaseEvent event, Acknowledgment acknowledgment) {

        try {
            if (event.getData() == null) {
                LOGGER.error("PurchaseCreateEvent received, but attribute data is null.");
                throw new EventProcessingException("PurchaseCreateEvent received, but attribute data is null.");
            }

            LOGGER.info("Event Received: Topic[{}], Data[{}]", kafkaConfigData.getPurchaseCreateTopic(), event.toString());

            purchaseItemService.processPurchaseEvent(event.getData());

        } catch (EventProcessingException ex) {
            //TODO: What should be done? Save to reprocess later or manually?
            LOGGER.error(ex.getMessage());
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
