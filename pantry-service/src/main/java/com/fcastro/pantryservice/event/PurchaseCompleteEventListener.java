package com.fcastro.pantryservice.event;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.event.PurchaseCompleteEvent;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.pantryservice.pantryitem.PantryItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring", value = "kafka.enabled", matchIfMissing = true, havingValue = "true")
public class PurchaseCompleteEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseCompleteEventListener.class);

    private final PantryItemService pantryItemService;

    private final KafkaConfigData kafkaConfigData;

    public PurchaseCompleteEventListener(PantryItemService pantryItemService, KafkaConfigData kafkaConfigData) {
        this.pantryItemService = pantryItemService;
        this.kafkaConfigData = kafkaConfigData;
    }

    //TODO: HOW TO define <topics> getting value from configuration files?
    @KafkaListener(topics = "purchaseCompleteTopic", containerFactory = "kafkaListenerContainerFactory")
    protected void listener(PurchaseCompleteEvent event, Acknowledgment acknowledgment) {

        try {

            if (event.getData() == null) {
                LOG.error("purchaseCompleteTopic received, but attribute data is null.");
            }

            LOG.info("Event Received: Topic[{}], Data[{}]", kafkaConfigData.getPurchaseCompleteTopic(), event.getData().toString());
            pantryItemService.processPurchaseCompleteEvent(event.getData());

        } catch (EventProcessingException ex) {
            //TODO: What should be done? Save to reprocess later or manually?
            StringBuilder sb = new StringBuilder();
            ex.getThrowableMap().entrySet().forEach(
                    (entry) -> {
                        sb.append(entry.getKey().toString()).append(" - ").append(entry.getValue().toString());
                    }
            );
            LOG.error(ex.getMessage() + "List of Item and Error: {}", sb);

        } finally {
            acknowledgment.acknowledge();
        }
    }
}
